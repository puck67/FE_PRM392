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
import com.example.prm.models.ServiceListResponse;
import com.example.prm.network.ApiClient;
import com.example.prm.network.ServiceApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceListActivity extends AppCompatActivity implements ServiceAdapter.OnServiceSelectionChangedListener {

    private static final String TAG = "ServiceListActivity";

    // Views
    private Toolbar toolbar;
    private TextView tvCenterInfo;
    private RecyclerView rvServices;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private View layoutSummary;
    private TextView tvSelectedCount;
    private TextView tvTotalDuration;
    private TextView tvTotalPrice;
    private Button btnContinue;

    // Data & API
    private ServiceAdapter adapter;
    private ServiceApiService apiService;
    private SharedPreferences sharedPreferences;

    // Center info
    private int selectedCenterId;
    private String selectedCenterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        // Get selected center info
        selectedCenterId = getIntent().getIntExtra("selected_center_id", -1);
        selectedCenterName = getIntent().getStringExtra("selected_center_name");

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        initApi();
        loadServices();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvCenterInfo = findViewById(R.id.tv_center_info);
        rvServices = findViewById(R.id.rv_services);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutError = findViewById(R.id.layout_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);
        layoutSummary = findViewById(R.id.layout_summary);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        tvTotalDuration = findViewById(R.id.tv_total_duration);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn dịch vụ");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new ServiceAdapter();
        adapter.setOnServiceSelectionChangedListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnRetry.setOnClickListener(v -> loadServices());

        btnContinue.setOnClickListener(v -> {
            List<Service> selectedServices = adapter.getSelectedServices();
            if (selectedServices.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 dịch vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển sang bước 4 - Chọn ngày và giờ
            Intent intent = new Intent(this, DateTimeSelectionActivity.class);
            intent.putExtra("selected_center_id", selectedCenterId);
            intent.putExtra("selected_center_name", selectedCenterName);
            intent.putExtra("selected_services", new ArrayList<>(selectedServices));
            startActivity(intent);
        });
    }

    private void initApi() {
        apiService = ApiClient.getRetrofitInstance().create(ServiceApiService.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
    }

    private void loadServices() {
        showLoadingState();

        // Thử gọi API không cần auth trước
        Call<ServiceListResponse> call = apiService.getActiveServices(1, 50, null, null);

        call.enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessResponse(response.body());
                } else if (response.code() == 401) {
                    // Nếu 401, thử với token
                    loadServicesWithAuth();
                } else {
                    handleErrorResponse("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                handleErrorResponse("Không thể kết nối đến server");
            }
        });
    }

    private void loadServicesWithAuth() {
        String jwtToken = sharedPreferences.getString("token", "");
        Log.d(TAG, "JWT Token from SharedPrefs: " + (jwtToken.isEmpty() ? "EMPTY" : "EXISTS"));
        
        if (jwtToken.isEmpty()) {
            handleErrorResponse("Chưa đăng nhập. Vui lòng đăng nhập lại.");
            return;
        }
        
        String token = "Bearer " + jwtToken;
        Log.d(TAG, "Authorization Header: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        
        Call<ServiceListResponse> call = apiService.getActiveServicesWithAuth(
            token, 1, 50, null, null);

        call.enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessResponse(response.body());
                } else if (response.code() == 401) {
                    handleErrorResponse("Token đã hết hạn. Vui lòng đăng nhập lại.");
                } else {
                    handleErrorResponse("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                Log.e(TAG, "API call with auth failed", t);
                handleErrorResponse("Không thể kết nối đến server");
            }
        });
    }

    private void handleSuccessResponse(ServiceListResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            List<Service> services = response.getData().getServices();
            
            // Show center info
            tvCenterInfo.setText("Trung tâm: " + selectedCenterName);
            
            if (services.isEmpty()) {
                showEmptyState();
            } else {
                adapter.updateServices(services);
                showContentState();
            }
        } else {
            String errorMessage = response.getMessage();
            if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                errorMessage = response.getErrors().get(0);
            }
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
        rvServices.setVisibility(View.GONE);
        layoutSummary.setVisibility(View.GONE);
    }

    private void showContentState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvServices.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvServices.setVisibility(View.GONE);
        layoutSummary.setVisibility(View.GONE);
    }

    private void showErrorState(String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        rvServices.setVisibility(View.GONE);
        layoutSummary.setVisibility(View.GONE);
        tvErrorMessage.setText(errorMessage);
    }

    @Override
    public void onServiceSelectionChanged(List<Service> selectedServices, double totalPrice, int totalDuration) {
        if (selectedServices.isEmpty()) {
            layoutSummary.setVisibility(View.GONE);
        } else {
            layoutSummary.setVisibility(View.VISIBLE);
            
            // Update summary info
            tvSelectedCount.setText(selectedServices.size() + " dịch vụ");
            
            // Format duration
            String durationText;
            if (totalDuration >= 60) {
                int hours = totalDuration / 60;
                int minutes = totalDuration % 60;
                if (minutes > 0) {
                    durationText = hours + "h " + minutes + "p";
                } else {
                    durationText = hours + " giờ";
                }
            } else {
                durationText = totalDuration + " phút";
            }
            tvTotalDuration.setText(durationText);
            
            // Format price
            tvTotalPrice.setText(String.format("%,.0f VNĐ", totalPrice));
        }
    }
}
