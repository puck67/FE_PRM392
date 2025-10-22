package com.example.prm.ui.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;
import com.example.prm.models.Service;
import com.example.prm.models.Vehicle;
import com.example.prm.models.VehicleListResponse;
import com.example.prm.network.ApiClient;
import com.example.prm.network.VehicleApiService;
import com.example.prm.ui.booking.BookingConfirmationActivity;
import com.example.prm.ui.vehicle.AddVehicleActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleSelectionActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private static final String TAG = "VehicleSelectionActivity";

    // Views
    private Toolbar toolbar;
    private TextView tvBookingSummary;
    private RecyclerView rvVehicles;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private Button btnAddVehicle;
    private Button btnAddVehicleEmpty;
    private Button btnContinue;

    // Data & API
    private VehicleAdapter vehicleAdapter;
    private VehicleApiService apiService;
    private SharedPreferences sharedPreferences;

    // Booking info
    private int selectedCenterId;
    private String selectedCenterName;
    private ArrayList<Service> selectedServices;
    private String selectedDate;
    private String selectedTimeSlot;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_selection);

        // Get booking data from previous activity
        getIntentData();
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        initApi();
        
        updateBookingSummary();
        loadVehicles();
    }

    private void getIntentData() {
        selectedCenterId = getIntent().getIntExtra("selected_center_id", -1);
        selectedCenterName = getIntent().getStringExtra("selected_center_name");
        selectedServices = (ArrayList<Service>) getIntent().getSerializableExtra("selected_services");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTimeSlot = getIntent().getStringExtra("selected_time_slot");
        
        Log.d(TAG, "Received booking data:");
        Log.d(TAG, "Center: " + selectedCenterName + " (ID: " + selectedCenterId + ")");
        Log.d(TAG, "Services: " + (selectedServices != null ? selectedServices.size() : 0));
        Log.d(TAG, "DateTime: " + selectedDate + " " + selectedTimeSlot);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvBookingSummary = findViewById(R.id.tv_booking_summary);
        rvVehicles = findViewById(R.id.rv_vehicles);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutError = findViewById(R.id.layout_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);
        btnAddVehicle = findViewById(R.id.btn_add_vehicle);
        btnAddVehicleEmpty = findViewById(R.id.btn_add_vehicle_empty);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn xe");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        vehicleAdapter = new VehicleAdapter();
        vehicleAdapter.setOnVehicleClickListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvVehicles.setLayoutManager(layoutManager);
        rvVehicles.setAdapter(vehicleAdapter);
    }

    private void setupClickListeners() {
        btnRetry.setOnClickListener(v -> loadVehicles());
        
        btnAddVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVehicleActivity.class);
            startActivityForResult(intent, 100);
        });
        
        btnAddVehicleEmpty.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVehicleActivity.class);
            startActivityForResult(intent, 100);
        });

        btnContinue.setOnClickListener(v -> {
            Vehicle selectedVehicle = vehicleAdapter.getSelectedVehicle();
            if (selectedVehicle == null) {
                Toast.makeText(this, "Vui lòng chọn xe", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển sang bước 6 - Xác nhận thông tin booking
            Intent intent = new Intent(this, BookingConfirmationActivity.class);
            intent.putExtra("selected_center_id", selectedCenterId);
            intent.putExtra("selected_center_name", selectedCenterName);
            intent.putExtra("selected_services", selectedServices);
            intent.putExtra("selected_date", selectedDate);
            intent.putExtra("selected_time_slot", selectedTimeSlot);
            intent.putExtra("selected_vehicle", selectedVehicle);
            startActivity(intent);
        });
    }

    private void initApi() {
        apiService = ApiClient.getRetrofitInstance().create(VehicleApiService.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        
        // Get current user ID from SharedPreferences
        String userIdStr = sharedPreferences.getString("user_id", "0");
        try {
            currentUserId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            currentUserId = 0;
        }
    }

    private void updateBookingSummary() {
        if (selectedServices != null && !selectedServices.isEmpty()) {
            String dateFormatted = formatDate(selectedDate);
            String summary = selectedCenterName + " • " + dateFormatted + " " + selectedTimeSlot + 
                           " • " + selectedServices.size() + " dịch vụ";
            tvBookingSummary.setText(summary);
        }
    }

    private String formatDate(String date) {
        // Convert 2025-10-20 to 20/10/2025
        try {
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return date;
        }
    }

    private void loadVehicles() {
        if (currentUserId == 0) {
            // If no user ID, show empty state
            showEmptyState();
            return;
        }

        showLoadingState();

        String token = "Bearer " + sharedPreferences.getString("token", "");
        
        Call<VehicleListResponse> call = apiService.getCustomerVehicles(
            token, currentUserId, 1, 50);

        call.enqueue(new Callback<VehicleListResponse>() {
            @Override
            public void onResponse(Call<VehicleListResponse> call, Response<VehicleListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessResponse(response.body());
                } else {
                    handleErrorResponse("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VehicleListResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                handleErrorResponse("Không thể kết nối đến server");
            }
        });
    }

    private void handleSuccessResponse(VehicleListResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            List<Vehicle> vehicles = response.getData().getVehicles();
            
            if (vehicles.isEmpty()) {
                showEmptyState();
            } else {
                vehicleAdapter.updateVehicles(vehicles);
                showContentState();
            }
        } else {
            String errorMessage = response.getMessage() != null ? response.getMessage() : "Có lỗi xảy ra";
            handleErrorResponse(errorMessage);
        }
    }

    private void handleErrorResponse(String errorMessage) {
        showErrorState(errorMessage);
    }

    private void showLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvVehicles.setVisibility(View.GONE);
    }

    private void showContentState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvVehicles.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvVehicles.setVisibility(View.GONE);
    }

    private void showErrorState(String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        rvVehicles.setVisibility(View.GONE);
        tvErrorMessage.setText(errorMessage);
    }

    @Override
    public void onVehicleSelect(Vehicle vehicle) {
        // Enable continue button when vehicle is selected
        btnContinue.setEnabled(true);
        btnContinue.setAlpha(1.0f);
        
        Log.d(TAG, "Selected vehicle: " + vehicle.getFullInfo());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Reload vehicles after adding new vehicle
            loadVehicles();
        }
    }
}
