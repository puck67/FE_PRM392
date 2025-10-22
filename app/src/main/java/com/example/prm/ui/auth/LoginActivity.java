package com.example.prm.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm.R;
import com.example.prm.databinding.ActivityLoginBinding;
import com.example.prm.models.ApiResponse;
import com.example.prm.models.LoginRequest;
import com.example.prm.models.LoginResponse;
import com.example.prm.network.ApiClient;
import com.example.prm.ui.home.HomeActivity;
import com.example.prm.ui.booking.VehicleSelectionActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupViews();
        setupClickListeners();
    }
    
    private void setupViews() {
        // Password visibility toggle
        binding.ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
    }
    
    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        binding.tvRegister.setOnClickListener(v -> navigateToRegister());
    }
    
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.ivPasswordToggle.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.ivPasswordToggle.setImageResource(R.drawable.ic_eye_on);
            isPasswordVisible = true;
        }
        // Move cursor to end
        binding.etPassword.setSelection(binding.etPassword.getText().length());
    }
    
    private void performLogin() {
        String emailOrPhone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        if (validateInput(emailOrPhone, password)) {
            showLoadingState(true);
            
            LoginRequest loginRequest = new LoginRequest(emailOrPhone, password);
            
            ApiClient.getApiService().login(loginRequest)
                    .enqueue(new Callback<ApiResponse<LoginResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                            showLoadingState(false);
                            
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<LoginResponse> apiResponse = response.body();
                                
                                if (apiResponse.isSuccess()) {
                                    LoginResponse loginResponse = apiResponse.getData();
                                    saveUserData(loginResponse);
                                    navigateToHome();
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("Đăng nhập thất bại. Vui lòng thử lại.");
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                            showLoadingState(false);
                            showError("Lỗi kết nối. Vui lòng kiểm tra internet và thử lại.");
                        }
                    });
        }
    }
    
    private boolean validateInput(String emailOrPhone, String password) {
        if (emailOrPhone.isEmpty()) {
            binding.etPhone.setError("Vui lòng nhập số điện thoại hoặc email");
            binding.etPhone.requestFocus();
            return false;
        }
        
        // Kiểm tra xem có phải email hay số điện thoại
        boolean isEmail = emailOrPhone.contains("@");
        boolean isPhone = emailOrPhone.matches("^0\\d{9}$");
        
        if (!isEmail && !isPhone) {
            binding.etPhone.setError("Vui lòng nhập email hợp lệ hoặc số điện thoại (0xxxxxxxxx)");
            binding.etPhone.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            binding.etPassword.setError("Vui lòng nhập mật khẩu");
            binding.etPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showLoadingState(boolean isLoading) {
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnLogin.setText(isLoading ? "Đang đăng nhập..." : "Đăng nhập");
    }
    
    private void saveUserData(LoginResponse loginResponse) {
        // Lưu token và thông tin user vào SharedPreferences
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("token", loginResponse.getToken())
                .putString("refresh_token", loginResponse.getRefreshToken())
                .putString("user_name", loginResponse.getUser().getFullName())
                .putString("user_email", loginResponse.getUser().getEmail())
                .putInt("user_id", loginResponse.getUser().getId())
                .putBoolean("is_logged_in", true)
                .apply();
    }
    
    private void navigateToHome() {
        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        
        // TODO: Check if user has vehicles, for now assume they have
        // If no vehicle -> VehicleSelectionActivity
        // If has vehicle -> HomeActivity
        
        // For demo, navigate to Home (assuming user has vehicle)
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    
    private void showForgotPasswordDialog() {
        Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
