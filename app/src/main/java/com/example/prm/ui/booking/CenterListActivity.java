package com.example.prm.ui.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;
import com.example.prm.models.Center;
import com.example.prm.models.CenterListResponse;
import com.example.prm.network.ApiClient;
import com.example.prm.network.CenterApiService;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CenterListActivity extends AppCompatActivity implements CenterAdapter.OnCenterClickListener {

    private static final String TAG = "CenterListActivity";
    
    // Views
    private Toolbar toolbar;
    private EditText etSearch;
    private Spinner spinnerCity;
    private Button btnFilter;
    private RecyclerView rvCenters;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private Button btnRetryError;

    // Data & API
    private CenterAdapter adapter;
    private CenterApiService apiService;
    private SharedPreferences sharedPreferences;
    
    // Filter parameters
    private String currentSearchTerm = "";
    private String currentCity = "";
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_list);

        initViews();
        setupToolbar();
        setupSpinner();
        setupRecyclerView();
        setupSearchFilter();
        setupClickListeners();
        initApi();
        loadCenters();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.et_search);
        spinnerCity = findViewById(R.id.spinner_city);
        btnFilter = findViewById(R.id.btn_filter);
        rvCenters = findViewById(R.id.rv_centers);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutError = findViewById(R.id.layout_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);
        btnRetryError = findViewById(R.id.btn_retry_error);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSpinner() {
        String[] cities = {"Tất cả thành phố", "Hà Nội", "TP.HCM", "Đà Nẵng", "Cần Thơ", "Hải Phòng"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
    }

    private void setupRecyclerView() {
        adapter = new CenterAdapter();
        adapter.setOnCenterClickListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCenters.setLayoutManager(layoutManager);
        rvCenters.setAdapter(adapter);

        // Add pagination scroll listener
        rvCenters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (!isLoading && !isLastPage) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() >= 
                        adapter.getItemCount() - 1) {
                        loadMoreCenters();
                    }
                }
            }
        });
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchTerm = s.toString().trim();
                resetPagination();
                loadCenters();
            }
        });
    }

    private void setupClickListeners() {
        btnFilter.setOnClickListener(v -> {
            String selectedCity = spinnerCity.getSelectedItem().toString();
            currentCity = selectedCity.equals("Tất cả thành phố") ? "" : selectedCity;
            resetPagination();
            loadCenters();
        });

        btnRetry.setOnClickListener(v -> {
            resetPagination();
            loadCenters();
        });

        btnRetryError.setOnClickListener(v -> {
            resetPagination();
            loadCenters();
        });
    }

    private void initApi() {
        apiService = ApiClient.getRetrofitInstance().create(CenterApiService.class);
        sharedPreferences = getSharedPreferences("EV_SERVICE_PREFS", MODE_PRIVATE);
    }

    private void loadCenters() {
        if (isLoading) return;
        
        isLoading = true;
        showLoadingState();

        Call<CenterListResponse> call = apiService.getCenters(
            currentPage, pageSize, currentSearchTerm, currentCity);

        call.enqueue(new Callback<CenterListResponse>() {
            @Override
            public void onResponse(Call<CenterListResponse> call, Response<CenterListResponse> response) {
                isLoading = false;
                
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessResponse(response.body());
                } else {
                    handleErrorResponse("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CenterListResponse> call, Throwable t) {
                isLoading = false;
                Log.e(TAG, "API call failed", t);
                handleErrorResponse("Không thể kết nối đến server");
            }
        });
    }

    private void loadMoreCenters() {
        if (isLoading || isLastPage) return;
        
        currentPage++;
        loadCenters();
    }

    private void handleSuccessResponse(CenterListResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            List<Center> centers = response.getData().getItems();
            
            if (currentPage == 1) {
                adapter.updateCenters(centers);
            } else {
                adapter.addCenters(centers);
            }

            // Check if last page
            isLastPage = currentPage >= response.getData().getTotalPages();
            
            // Show appropriate state
            if (centers.isEmpty() && currentPage == 1) {
                showEmptyState();
            } else {
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
        if (currentPage == 1) {
            showErrorState(errorMessage);
        } else {
            // Reset page for retry
            currentPage--;
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPagination() {
        currentPage = 1;
        isLastPage = false;
    }

    private void showLoadingState() {
        if (currentPage == 1) {
            layoutLoading.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            rvCenters.setVisibility(View.GONE);
        }
    }

    private void showContentState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvCenters.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvCenters.setVisibility(View.GONE);
    }

    private void showErrorState(String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        rvCenters.setVisibility(View.GONE);
        tvErrorMessage.setText(errorMessage);
    }

    @Override
    public void onCenterSelect(Center center) {
        // Lưu trung tâm đã chọn và chuyển đến màn hình chọn dịch vụ
        Intent intent = new Intent(this, ServiceListActivity.class);
        intent.putExtra("selected_center_id", center.getId());
        intent.putExtra("selected_center_name", center.getName());
        startActivity(intent);
    }

    @Override
    public void onCenterDetails(Center center) {
        // Chuyển đến màn hình chi tiết trung tâm
        Intent intent = new Intent(this, CenterDetailActivity.class);
        intent.putExtra("center_id", center.getId());
        startActivity(intent);
    }
}
