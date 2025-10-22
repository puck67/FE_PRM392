package com.example.prm.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm.R;
import com.example.prm.models.ApiResponse;
import com.example.prm.models.User;
import com.example.prm.network.ApiClient;
import com.example.prm.network.ProfileApiService;
import com.example.prm.ui.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    // UI Components
    private Toolbar toolbar;
    private ScrollView scrollProfileContent;
    private LinearLayout layoutLoading;
    private LinearLayout layoutError;
    private TextView tvErrorMessage;
    private Button btnRetry;

    // Profile Views
    private ImageView ivAvatar;
    private TextView tvFullName;
    private TextView tvUserId;
    private TextView tvRole;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvDateOfBirth;
    private TextView tvGender;
    private TextView tvAddress;

    // Action Buttons
    private Button btnEditProfile;
    private Button btnChangePassword;
    private Button btnLogout;

    // API and Preferences
    private ProfileApiService profileApiService;
    private SharedPreferences sharedPreferences;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupToolbar();
        initServices();
        setupClickListeners();
        loadProfile();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        scrollProfileContent = findViewById(R.id.scroll_profile_content);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutError = findViewById(R.id.layout_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);

        // Profile Views
        ivAvatar = findViewById(R.id.iv_avatar);
        tvFullName = findViewById(R.id.tv_full_name);
        tvUserId = findViewById(R.id.tv_user_id);
        tvRole = findViewById(R.id.tv_role);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvDateOfBirth = findViewById(R.id.tv_date_of_birth);
        tvGender = findViewById(R.id.tv_gender);
        tvAddress = findViewById(R.id.tv_address);

        // Action Buttons
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initServices() {
        profileApiService = ApiClient.getProfileApiService();
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
    }

    private void setupClickListeners() {
        btnRetry.setOnClickListener(v -> loadProfile());
        
        btnEditProfile.setOnClickListener(v -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(this, EditProfileActivity.class);
            if (currentUser != null) {
                intent.putExtra("user", currentUser);
            }
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            // Navigate to ChangePasswordActivity
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    private void loadProfile() {
        String token = sharedPreferences.getString("token", "");
        Log.d(TAG, "Token found: " + (!token.isEmpty()));
        Log.d(TAG, "Token length: " + token.length());
        
        if (token.isEmpty()) {
            Log.e(TAG, "No token found in SharedPreferences");
            showErrorState("Phiên đăng nhập đã hết hạn");
            return;
        }

        showLoadingState();
        Log.d(TAG, "Making API call to /auth/profile");

        Call<ApiResponse<User>> call = profileApiService.getProfile("Bearer " + token);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                Log.d(TAG, "API Response code: " + response.code());
                Log.d(TAG, "API Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    Log.d(TAG, "API Response success: " + apiResponse.isSuccess());
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        currentUser = apiResponse.getData();
                        displayProfile(currentUser);
                        showContentState();
                        Log.d(TAG, "Profile loaded successfully for user: " + currentUser.getFullName());
                    } else {
                        Log.e(TAG, "API Response failed: " + apiResponse.getMessage());
                        showErrorState(apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Không thể tải thông tin profile");
                    }
                } else if (response.code() == 401) {
                    Log.e(TAG, "Unauthorized - token expired or invalid");
                    handleUnauthorized();
                } else {
                    Log.e(TAG, "API Response failed with code: " + response.code());
                    showErrorState("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Failed to load profile", t);
                showErrorState("Lỗi kết nối mạng");
            }
        });
    }

    private void displayProfile(User user) {
        tvFullName.setText(user.getFullName() != null ? user.getFullName() : "Chưa cập nhật");
        tvUserId.setText("ID: " + user.getId());
        tvRole.setText(user.getRoleDisplay());
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa cập nhật");
        tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "Chưa cập nhật");
        tvDateOfBirth.setText(user.getFormattedDateOfBirth() != null ? 
            user.getFormattedDateOfBirth() : "Chưa cập nhật");
        tvGender.setText(user.getGenderDisplay());
        tvAddress.setText(user.getAddress() != null ? user.getAddress() : "Chưa cập nhật");

        // Load avatar (simple fallback without Glide)
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            // TODO: Add image loading library (Glide/Picasso) in build.gradle for URL loading
            // For now, just use default avatar
            ivAvatar.setImageResource(R.drawable.ic_account);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_account);
        }
    }

    private void showLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        scrollProfileContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showContentState() {
        layoutLoading.setVisibility(View.GONE);
        scrollProfileContent.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
    }

    private void showErrorState(String message) {
        layoutLoading.setVisibility(View.GONE);
        scrollProfileContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }

    private void handleUnauthorized() {
        // Clear token and redirect to login
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.remove("user_id");
        editor.apply();

        Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        // Clear all stored data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile when returning from edit screen
        if (currentUser != null) {
            loadProfile();
        }
    }
}
