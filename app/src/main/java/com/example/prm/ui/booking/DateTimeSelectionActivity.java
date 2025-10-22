package com.example.prm.ui.booking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;
import com.example.prm.models.AvailabilityResponse;
import com.example.prm.models.Service;
import com.example.prm.network.ApiClient;
import com.example.prm.network.BookingApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateTimeSelectionActivity extends AppCompatActivity implements TimeSlotAdapter.OnTimeSlotClickListener {

    private static final String TAG = "DateTimeSelectionActivity";

    // Views
    private Toolbar toolbar;
    private TextView tvCenterInfo;
    private TextView tvSelectedServices;
    private CalendarView calendarView;
    private TextView tvSelectedDate;
    private RecyclerView rvTimeSlots;
    private View layoutLoading;
    private View layoutEmpty;
    private View layoutError;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private Button btnContinue;

    // Data & API
    private TimeSlotAdapter timeSlotAdapter;
    private BookingApiService apiService;
    private SharedPreferences sharedPreferences;

    // Booking info
    private int selectedCenterId;
    private String selectedCenterName;
    private ArrayList<Service> selectedServices;
    private String selectedDate;
    private String selectedTimeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_selection);

        // Get data from previous activity
        getIntentData();
        
        initViews();
        setupToolbar();
        setupCalendar();
        setupRecyclerView();
        setupClickListeners();
        initApi();
        
        // Set default date to today
        setDefaultDate();
    }

    private void getIntentData() {
        selectedCenterId = getIntent().getIntExtra("selected_center_id", -1);
        selectedCenterName = getIntent().getStringExtra("selected_center_name");
        selectedServices = (ArrayList<Service>) getIntent().getSerializableExtra("selected_services");
        
        Log.d(TAG, "Received centerId: " + selectedCenterId);
        Log.d(TAG, "Received centerName: " + selectedCenterName);
        Log.d(TAG, "Received services count: " + (selectedServices != null ? selectedServices.size() : 0));
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvCenterInfo = findViewById(R.id.tv_center_info);
        tvSelectedServices = findViewById(R.id.tv_selected_services);
        calendarView = findViewById(R.id.calendar_view);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        rvTimeSlots = findViewById(R.id.rv_time_slots);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutError = findViewById(R.id.layout_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn ngày và giờ");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupCalendar() {
        // Set minimum date to today
        Calendar today = Calendar.getInstance();
        calendarView.setMinDate(today.getTimeInMillis());
        
        // Set maximum date to 30 days from now
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 30);
        calendarView.setMaxDate(maxDate.getTimeInMillis());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selected.getTime());
            updateSelectedDate();
            loadAvailableSlots();
        });
    }

    private void setupRecyclerView() {
        timeSlotAdapter = new TimeSlotAdapter();
        timeSlotAdapter.setOnTimeSlotClickListener(this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvTimeSlots.setLayoutManager(layoutManager);
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void setupClickListeners() {
        btnRetry.setOnClickListener(v -> loadAvailableSlots());

        btnContinue.setOnClickListener(v -> {
            if (selectedTimeSlot == null || selectedTimeSlot.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn giờ hẹn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển sang bước 5 - Chọn thông tin xe
            Intent intent = new Intent(this, VehicleSelectionActivity.class);
            intent.putExtra("selected_center_id", selectedCenterId);
            intent.putExtra("selected_center_name", selectedCenterName);
            intent.putExtra("selected_services", selectedServices);
            intent.putExtra("selected_date", selectedDate);
            intent.putExtra("selected_time_slot", selectedTimeSlot);
            startActivity(intent);
        });
    }

    private void initApi() {
        apiService = ApiClient.getRetrofitInstance().create(BookingApiService.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
    }

    private void setDefaultDate() {
        // Set today as default
        Calendar today = Calendar.getInstance();
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.getTime());
        
        // Update info displays
        updateBookingInfo();
        updateSelectedDate();
        
        // Load slots for today
        loadAvailableSlots();
    }

    private void updateBookingInfo() {
        // Update center info
        tvCenterInfo.setText("Trung tâm: " + selectedCenterName);
        
        // Update selected services
        if (selectedServices != null && !selectedServices.isEmpty()) {
            StringBuilder servicesText = new StringBuilder();
            double totalPrice = 0;
            
            for (int i = 0; i < selectedServices.size(); i++) {
                Service service = selectedServices.get(i);
                servicesText.append(service.getServiceName());
                totalPrice += service.getBasePrice();
                
                if (i < selectedServices.size() - 1) {
                    servicesText.append(", ");
                }
            }
            
            servicesText.append(String.format(" (Tổng: %,.0f VNĐ)", totalPrice));
            tvSelectedServices.setText(servicesText.toString());
        }
    }

    private void updateSelectedDate() {
        if (selectedDate != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
                Date date = inputFormat.parse(selectedDate);
                tvSelectedDate.setText("Ngày đã chọn: " + outputFormat.format(date));
            } catch (Exception e) {
                tvSelectedDate.setText("Ngày đã chọn: " + selectedDate);
            }
        }
    }

    private void loadAvailableSlots() {
        if (selectedDate == null || selectedCenterId == -1 || selectedServices == null) {
            return;
        }

        showLoadingState();

        // Prepare serviceIds
        List<Integer> serviceIds = new ArrayList<>();
        for (Service service : selectedServices) {
            serviceIds.add(service.getServiceId());
        }

        String token = "Bearer " + sharedPreferences.getString("token", "");
        String serviceIdsString = android.text.TextUtils.join(",", serviceIds);

        Call<AvailabilityResponse> call = apiService.getAvailability(
            token, selectedCenterId, selectedDate, serviceIdsString);

        call.enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessResponse(response.body());
                } else if (response.code() == 500) {
                    // Backend database error, use mock data for demo
                    Log.w(TAG, "Backend error 500, using mock data");
                    loadMockTimeSlots();
                } else {
                    handleErrorResponse("Lỗi kết nối: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                // Fallback to mock data for demo
                loadMockTimeSlots();
            }
        });
    }

    private void handleSuccessResponse(AvailabilityResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            List<String> availableSlots = response.getData().getAvailableSlots();
            
            if (availableSlots.isEmpty()) {
                showEmptyState();
            } else {
                timeSlotAdapter.updateTimeSlots(availableSlots);
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

    private void loadMockTimeSlots() {
        // Mock available time slots for demo
        List<String> mockSlots = Arrays.asList(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00"
        );
        
        Log.d(TAG, "Using mock time slots: " + mockSlots.size() + " slots");
        timeSlotAdapter.updateTimeSlots(mockSlots);
        showContentState();
    }

    private void showLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvTimeSlots.setVisibility(View.GONE);
    }

    private void showContentState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        rvTimeSlots.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        rvTimeSlots.setVisibility(View.GONE);
    }

    private void showErrorState(String errorMessage) {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        rvTimeSlots.setVisibility(View.GONE);
        tvErrorMessage.setText(errorMessage);
    }

    @Override
    public void onTimeSlotClick(String timeSlot, boolean isSelected) {
        if (isSelected) {
            selectedTimeSlot = timeSlot;
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1.0f);
        } else {
            selectedTimeSlot = null;
            btnContinue.setEnabled(false);
            btnContinue.setAlpha(0.5f);
        }
    }
}
