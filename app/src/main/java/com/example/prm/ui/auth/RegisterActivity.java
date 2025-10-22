package com.example.prm.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm.R;
import com.example.prm.databinding.ActivityRegisterBinding;
import com.example.prm.models.ApiResponse;
import com.example.prm.models.RegisterRequest;
import com.example.prm.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    
    private ActivityRegisterBinding binding;
    private boolean isPasswordVisible = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupViews();
        setupClickListeners();
        setupPasswordValidation();
    }
    
    private void setupViews() {
        // Password visibility toggle
        binding.ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
    }
    
    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> performRegister());
    }
    
    private void setupPasswordValidation() {
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordRules(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void validatePasswordRules(String password) {
        // Update validation UI based on password rules
        // This is a simplified version - you can enhance this with proper UI updates
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
    
    private void performRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        
        if (validateInput(fullName, email, password, confirmPassword, phoneNumber)) {
            showLoadingState(true);
            
            // Create register request with user input
            RegisterRequest registerRequest = new RegisterRequest(
                    fullName, // FullName from user input
                    email, // Email from user input
                    password,
                    confirmPassword,
                    phoneNumber, // Phone number from user input
                    "1990-01-01", // Default date of birth
                    "MALE", // Default gender
                    "Việt Nam", // Default address
                    "https://example.com/avatar.jpg" // Default avatarUrl
            );
            
            ApiClient.getApiService().register(registerRequest)
                    .enqueue(new Callback<ApiResponse<Object>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                            showLoadingState(false);
                            
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Object> apiResponse = response.body();
                                
                                if (apiResponse.isSuccess()) {
                                    showSuccess(apiResponse.getMessage());
                                    finish(); // Return to login screen
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("Đăng ký thất bại. Vui lòng thử lại.");
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                            showLoadingState(false);
                            showError("Lỗi kết nối. Vui lòng kiểm tra internet và thử lại.");
                        }
                    });
        }
    }
    
    private boolean validateInput(String fullName, String email, String password, String confirmPassword, String phoneNumber) {
        if (fullName.isEmpty()) {
            showError("Vui lòng nhập họ và tên");
            return false;
        }
        
        if (fullName.length() < 2 || fullName.length() > 100) {
            showError("Họ và tên phải từ 2-100 ký tự");
            return false;
        }
        
        if (email.isEmpty()) {
            showError("Vui lòng nhập email");
            return false;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showError("Email không hợp lệ");
            return false;
        }
        
        if (phoneNumber.isEmpty()) {
            showError("Vui lòng nhập số điện thoại");
            return false;
        }
        
        if (!phoneNumber.matches("^0\\d{9}$")) {
            showError("Số điện thoại phải bắt đầu bằng 0 và có đúng 10 số");
            return false;
        }
        
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu");
            return false;
        }
        
        if (!isPasswordValid(password)) {
            showError("Mật khẩu không đáp ứng yêu cầu");
            return false;
        }
        
        if (confirmPassword.isEmpty()) {
            showError("Vui lòng nhập xác nhận mật khẩu");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Xác nhận mật khẩu không khớp");
            return false;
        }
        
        if (!binding.cbTerms.isChecked()) {
            showError("Vui lòng đồng ý với điều khoản và chính sách");
            return false;
        }
        
        return true;
    }
    
    private boolean isPasswordValid(String password) {
        // Check password rules
        boolean hasMinLength = password.length() >= 8;
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@#*.,!].*");
        
        return hasMinLength && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    
    private void showLoadingState(boolean isLoading) {
        binding.btnRegister.setEnabled(!isLoading);
        binding.btnRegister.setText(isLoading ? "Đang đăng ký..." : "Đăng ký");
    }
    
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
