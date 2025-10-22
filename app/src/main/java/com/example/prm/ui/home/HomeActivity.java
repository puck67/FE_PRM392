package com.example.prm.ui.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.prm.R;
import com.example.prm.databinding.ActivityHomeBinding;
import com.example.prm.models.VehicleListResponse;
import com.example.prm.network.ApiClient;
import com.example.prm.network.VehicleApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    
    private ActivityHomeBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Hide action bar/title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setupBottomNavigation();
        
        // Load default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // If need refresh vehicles after add
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("refresh_vehicles", false)) {
            // Switch to Home tab
            if (binding != null && binding.bottomNavigation != null) {
                binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
            }
            // Notify HomeFragment to refresh via broadcast event
            Intent refresh = new Intent("com.example.prm.REFRESH_VEHICLES");
            sendBroadcast(refresh);
            // Optional: also call background fetch (for any global caches)
            loadVehiclesForCustomer();
        }
    }
    
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_vehicle) {
                selectedFragment = new VehicleFragment();
            } else if (itemId == R.id.nav_service) {
                selectedFragment = new ServiceFragment();
            } else if (itemId == R.id.nav_location) {
                selectedFragment = new LocationFragment();
            } else if (itemId == R.id.nav_message) {
                selectedFragment = new MessageFragment();
            }
            
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    private void loadVehiclesForCustomer() {
        try {
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String token = sp.getString("token", "");
            int customerId = sp.getInt("user_id", 0); // NOTE: nếu BE tách bảng, đổi sang customerId thật
            
            // Debug log
            android.util.Log.d("HomeActivity", "loadVehiclesForCustomer: token=" + (token.isEmpty() ? "empty" : "exists") + 
                ", customerId=" + customerId);
            
            // Debug: Log the actual API call URL
            String debugUrl = "GET /api/vehicle?customerId=null&pageNumber=1&pageSize=20&searchTerm=";
            android.util.Log.d("HomeActivity", "API Call: " + debugUrl);
            
            if (token.isEmpty() || customerId <= 0) {
                Toast.makeText(this, "Token hoặc CustomerId không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            VehicleApiService svc = ApiClient.getVehicleApiService();
            // Truyền null để BE tự map customer theo JWT
            svc.getCustomerVehicles("Bearer " + token, null, 1, 20, "")
                .enqueue(new Callback<VehicleListResponse>() {
                    @Override
                    public void onResponse(Call<VehicleListResponse> call, Response<VehicleListResponse> response) {
                        android.util.Log.d("HomeActivity", "getCustomerVehicles response: " + response.code());
                        
                        // Debug: Log response body for troubleshooting
                        if (response.body() != null) {
                            android.util.Log.d("HomeActivity", "Response body: " + response.body().toString());
                        }
                        
                        if (response.isSuccessful() && response.body() != null) {
                            VehicleListResponse.VehicleData data = response.body().getData();
                            if (data != null && data.getVehicles() != null) {
                                android.util.Log.d("HomeActivity", "Found " + data.getVehicles().size() + " vehicles");
                                Toast.makeText(HomeActivity.this, 
                                    "Tải được " + data.getVehicles().size() + " xe", Toast.LENGTH_SHORT).show();
                            } else {
                                android.util.Log.d("HomeActivity", "No vehicles found - data is null or vehicles list is null");
                                Toast.makeText(HomeActivity.this, "Không có xe nào", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Ghi log/Toast nhẹ nếu cần
                            String errorMsg = "Lỗi " + response.code();
                            if (response.body() != null && response.body().getMessage() != null) {
                                errorMsg += ": " + response.body().getMessage();
                                android.util.Log.e("HomeActivity", "API Error: " + response.body().getMessage());
                            }
                            Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VehicleListResponse> call, Throwable t) {
                        android.util.Log.e("HomeActivity", "getCustomerVehicles failed", t);
                        Toast.makeText(HomeActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Error in loadVehiclesForCustomer", e);
            Toast.makeText(this, "Lỗi khi tải danh sách xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
