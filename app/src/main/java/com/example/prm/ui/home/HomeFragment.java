package com.example.prm.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.prm.databinding.FragmentHomeBinding;
import com.example.prm.models.Vehicle;
import com.example.prm.models.VehicleListResponse;
import com.example.prm.network.ApiClient;
import com.example.prm.network.VehicleApiService;
import com.example.prm.ui.adapter.VehicleAdapter;
import com.example.prm.ui.auth.LoginActivity;
import com.example.prm.ui.booking.CenterListActivity;
import com.example.prm.ui.profile.ProfileActivity;
import com.example.prm.ui.vehicle.AddVehicleActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private VehicleAdapter vehicleAdapter;
    private VehicleApiService vehicleApiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        setupRecyclerView();
        
        // Check if need to refresh vehicles from AddVehicleActivity
        if (getActivity() != null && getActivity().getIntent().getBooleanExtra("refresh_vehicles", false)) {
            loadUserVehicles();
            getActivity().getIntent().removeExtra("refresh_vehicles"); // Clear flag
        }
    }
    
    private void setupViews() {
        // Load user info from SharedPreferences
        loadUserInfo();
        
        // Setup avatar click to navigate to profile
        binding.ivUserAvatar.setOnClickListener(v -> {
            // Check if user is logged in before going to profile
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            
            if (token.isEmpty()) {
                // User not logged in, go to login screen
                Toast.makeText(getActivity(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            } else {
                // User logged in, go to profile
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        
        // Setup add vehicle button click
        binding.btnAddVehicle.setOnClickListener(v -> {
            // Check if user is logged in before adding vehicle
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            
            if (token.isEmpty()) {
                // User not logged in, go to login screen
                Toast.makeText(getActivity(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            } else {
                // User logged in, go to add vehicle
                Intent intent = new Intent(getActivity(), AddVehicleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        vehicleApiService = ApiClient.getVehicleApiService();
        vehicleAdapter = new VehicleAdapter();
        
        binding.recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewVehicles.setAdapter(vehicleAdapter);
        
        vehicleAdapter.setOnVehicleClickListener(vehicle -> {
            // Handle vehicle click if needed
            Toast.makeText(getContext(), "Clicked: " + vehicle.getLicensePlate(), Toast.LENGTH_SHORT).show();
        });
        
        // Load vehicles on setup
        loadUserVehicles();
    }

    private void loadUserVehicles() {
        if (getActivity() == null) return;
        
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        int userId = sharedPreferences.getInt("user_id", 0);
        
        if (token.isEmpty() || userId <= 0) {
            // User not logged in, hide vehicle section
            binding.vehicleSection.setVisibility(View.GONE);
            return;
        }
        
        Call<VehicleListResponse> call = vehicleApiService.getCustomerVehicles("Bearer " + token, userId, 1, 10);
        call.enqueue(new Callback<VehicleListResponse>() {
            @Override
            public void onResponse(Call<VehicleListResponse> call, Response<VehicleListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VehicleListResponse vehicleResponse = response.body();
                    if (vehicleResponse.isSuccess() && vehicleResponse.getData() != null && 
                        vehicleResponse.getData().getVehicles() != null && 
                        !vehicleResponse.getData().getVehicles().isEmpty()) {
                        
                        // Show vehicle section and update adapter
                        binding.vehicleSection.setVisibility(View.VISIBLE);
                        vehicleAdapter.updateVehicles(vehicleResponse.getData().getVehicles());
                        
                        Log.d(TAG, "Loaded " + vehicleResponse.getData().getVehicles().size() + " vehicles");
                    } else {
                        // No vehicles, hide section
                        binding.vehicleSection.setVisibility(View.GONE);
                        Log.d(TAG, "No vehicles found for user");
                    }
                } else {
                    // Error loading vehicles
                    binding.vehicleSection.setVisibility(View.GONE);
                    Log.e(TAG, "Failed to load vehicles: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<VehicleListResponse> call, Throwable t) {
                binding.vehicleSection.setVisibility(View.GONE);
                Log.e(TAG, "Network error loading vehicles", t);
            }
        });
    }
    
    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        
        // Load user name and ID
        String userName = sharedPreferences.getString("user_name", "");
        int userId = sharedPreferences.getInt("user_id", 0);
        
        if (!userName.isEmpty()) {
            binding.tvUserName.setText("Xin chào, " + userName);
            binding.tvUserName2.setText(userName);
        } else {
            // Default values if not logged in
            binding.tvUserName.setText("Xin chào, khách");
            binding.tvUserName2.setText("khách");
        }
        
        if (userId > 0) {
            binding.tvUserId.setText("ID : " + userId);
        } else {
            binding.tvUserId.setText("ID : ----");
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload user info and vehicles when fragment becomes visible again
        if (binding != null) {
            loadUserInfo();
            loadUserVehicles();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
