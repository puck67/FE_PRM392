package com.example.prm.ui.vehicle;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm.ui.home.HomeActivity;
import com.example.prm.R;
import com.example.prm.models.ApiResponse;
import com.example.prm.models.CreateVehicleRequest;
import com.example.prm.models.Vehicle;
import com.example.prm.models.VehicleRequestWrapper;
import com.example.prm.network.ApiClient;
import com.example.prm.network.VehicleApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {
    private static final String TAG = "AddVehicleActivity";

    // UI Components
    private Toolbar toolbar;
    private ScrollView scrollFormContent;
    private LinearLayout layoutLoading;
    
    // Form Fields
    private TextInputEditText etLicensePlate; // VIN field
    private TextInputEditText etLicensePlateNumber; // License plate field  
    private TextInputEditText etColor;
    private TextInputEditText etCurrentMileage;
    private TextInputEditText etLastServiceDate;
    
    
    // Buttons
    private Button btnCancel;
    private Button btnSave;

    // API and Preferences
    private VehicleApiService vehicleApiService;
    private SharedPreferences sharedPreferences;
    private int currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        initViews();
        setupToolbar();
        initServices();
        setupDatePickers();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        scrollFormContent = findViewById(R.id.scroll_form_content);
        layoutLoading = findViewById(R.id.layout_loading);
        
        etLicensePlate = findViewById(R.id.et_license_plate); // VIN field
        etLicensePlateNumber = findViewById(R.id.et_license_plate_number); // License plate field
        etColor = findViewById(R.id.et_color);
        etCurrentMileage = findViewById(R.id.et_current_mileage);
        etLastServiceDate = findViewById(R.id.et_last_service_date);
        
        
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
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
        vehicleApiService = ApiClient.getVehicleApiService();
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("user_id", 0);
    }

    private void setupDatePickers() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        // Last Service Date picker
        etLastServiceDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etLastServiceDate.setText(displayFormat.format(calendar.getTime()));
                    etLastServiceDate.setTag(dateFormat.format(calendar.getTime())); // Store API format
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        
    }


    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveVehicle());
        
        // Temporary workaround: Add long click to show technical info
        btnSave.setOnLongClickListener(v -> {
            Toast.makeText(this, "Chức năng thêm xe đang được bảo trì do lỗi database schema", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void saveVehicle() {
        if (!validateForm()) {
            return;
        }

        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create vehicle request object
        String vinNumber = etLicensePlate.getText().toString().trim().toUpperCase();
        String licensePlate = etLicensePlateNumber.getText().toString().trim().toUpperCase();
        
        CreateVehicleRequest request = new CreateVehicleRequest();
        // Debug: Log current user ID
        Log.d(TAG, "Current User ID from SharedPreferences: " + currentUserId);
        
        // Set customerId - if 0, let backend determine from JWT token
        if (currentUserId > 0) {
            request.setCustomerId(currentUserId);
        } else {
            // If user ID is 0, set to 0 and let backend handle it
            request.setCustomerId(0);
            Log.w(TAG, "User ID is 0, backend will determine from JWT token");
        }
        
        request.setVin(vinNumber);
        request.setLicensePlate(licensePlate);
        request.setColor(etColor.getText().toString().trim());
        
        // Get current mileage from input, default to 0 if empty
        String mileageStr = etCurrentMileage.getText().toString().trim();
        int currentMileage = mileageStr.isEmpty() ? 0 : Integer.parseInt(mileageStr);
        request.setCurrentMileage(currentMileage);
        
        // Get dates from pickers (stored in tag as yyyy-MM-dd format)
        String lastServiceDate = etLastServiceDate.getTag() != null ? 
            etLastServiceDate.getTag().toString() : null;
        
        // Only set lastServiceDate if it's not empty
        if (lastServiceDate != null && !lastServiceDate.trim().isEmpty()) {
            request.setLastServiceDate(lastServiceDate);
        } else {
            request.setLastServiceDate(null); // Explicitly set to null
        }
        

        // Debug: Log request details
        Log.d(TAG, "Creating vehicle with data:");
        Log.d(TAG, "CustomerId: " + request.getCustomerId());
        Log.d(TAG, "VIN: " + request.getVin());
        Log.d(TAG, "LicensePlate: " + request.getLicensePlate());
        Log.d(TAG, "Color: " + request.getColor());
        Log.d(TAG, "CurrentMileage: " + request.getCurrentMileage());
        Log.d(TAG, "LastServiceDate: " + request.getLastServiceDate());

        showLoadingState();

        Call<ApiResponse<Vehicle>> call = vehicleApiService.createVehicle("Bearer " + token, request);
        call.enqueue(new Callback<ApiResponse<Vehicle>>() {
            @Override
            public void onResponse(Call<ApiResponse<Vehicle>> call, Response<ApiResponse<Vehicle>> response) {
                hideLoadingState();
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Vehicle> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AddVehicleActivity.this, 
                            "Thêm xe thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Chuyển về trang chủ và refresh danh sách xe
                        Intent intent = new Intent(AddVehicleActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("refresh_vehicles", true);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Có lỗi xảy ra khi thêm xe";
                        Toast.makeText(AddVehicleActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 400) {
                    // Debug: Log error response details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "400 Error Response: " + errorBody);
                            Toast.makeText(AddVehicleActivity.this, 
                                "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddVehicleActivity.this, 
                                "Thông tin xe không hợp lệ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing 400 response", e);
                        Toast.makeText(AddVehicleActivity.this, 
                            "Thông tin xe không hợp lệ", Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(AddVehicleActivity.this, 
                        "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 500) {
                    try {
                        // Try to parse server error message
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Server error 500: " + errorBody);
                            
                            // Check if it's the specific database error we know about
                            if (errorBody.contains("Invalid column name") || 
                                errorBody.contains("NextServiceDue") || 
                                errorBody.contains("PurchaseDate")) {
                                Toast.makeText(AddVehicleActivity.this, 
                                    "Tạm thời không thể thêm xe mới. Hệ thống đang được cập nhật.", 
                                    Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddVehicleActivity.this, 
                                    "Hệ thống đang bảo trì. Vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddVehicleActivity.this, 
                                "Hệ thống đang bảo trì. Vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing server response", e);
                        Toast.makeText(AddVehicleActivity.this, 
                            "Hệ thống đang bảo trì. Vui lòng thử lại sau.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddVehicleActivity.this, 
                        "Lỗi kết nối: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Vehicle>> call, Throwable t) {
                hideLoadingState();
                Log.e(TAG, "Failed to create vehicle", t);
                Toast.makeText(AddVehicleActivity.this, 
                    "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // VIN (Số khung)
        String vin = etLicensePlate.getText().toString().trim();
        if (TextUtils.isEmpty(vin)) {
            etLicensePlate.setError("Vui lòng nhập số khung VIN");
            isValid = false;
        } else if (vin.length() != 17) {
            etLicensePlate.setError("Số khung VIN phải có đúng 17 ký tự");
            isValid = false;
        }

        // License Plate (Biển số xe)
        String licensePlate = etLicensePlateNumber.getText().toString().trim();
        if (TextUtils.isEmpty(licensePlate)) {
            etLicensePlateNumber.setError("Vui lòng nhập biển số xe");
            isValid = false;
        }

        // Color
        String color = etColor.getText().toString().trim();
        if (TextUtils.isEmpty(color)) {
            etColor.setError("Vui lòng nhập màu sắc");
            isValid = false;
        }

        // Current Mileage (optional, default to 0)
        String mileageStr = etCurrentMileage.getText().toString().trim();
        if (!TextUtils.isEmpty(mileageStr)) {
            try {
                int mileage = Integer.parseInt(mileageStr);
                if (mileage < 0) {
                    etCurrentMileage.setError("Số km phải lớn hơn hoặc bằng 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etCurrentMileage.setError("Số km phải là số nguyên");
                isValid = false;
            }
        }

        // Last Service Date (optional - can be null in database)
        // No validation needed as it's optional

        return isValid;
    }

    private void showLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        scrollFormContent.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        layoutLoading.setVisibility(View.GONE);
        scrollFormContent.setVisibility(View.VISIBLE);
    }
}
