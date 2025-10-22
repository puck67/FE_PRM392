package com.example.prm.ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm.R;
import com.example.prm.models.Service;
import com.example.prm.models.Vehicle;

import java.util.ArrayList;

public class BookingConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "BookingConfirmationActivity";

    // Booking data
    private int selectedCenterId;
    private String selectedCenterName;
    private ArrayList<Service> selectedServices;
    private String selectedDate;
    private String selectedTimeSlot;
    private Vehicle selectedVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Get booking data from previous activity
        getIntentData();
        
        initViews();
        setupToolbar();
        displayBookingSummary();
    }

    private void getIntentData() {
        selectedCenterId = getIntent().getIntExtra("selected_center_id", -1);
        selectedCenterName = getIntent().getStringExtra("selected_center_name");
        selectedServices = (ArrayList<Service>) getIntent().getSerializableExtra("selected_services");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTimeSlot = getIntent().getStringExtra("selected_time_slot");
        selectedVehicle = (Vehicle) getIntent().getSerializableExtra("selected_vehicle");
    }

    private void initViews() {
        // Views will be initialized when layout is created
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Xác nhận đặt lịch");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void displayBookingSummary() {
        // TODO: Display complete booking summary
        // For now, show toast with booking info
        String summary = String.format("Đặt lịch tại %s\nNgày: %s %s\nXe: %s\nDịch vụ: %d", 
            selectedCenterName, selectedDate, selectedTimeSlot,
            selectedVehicle != null ? selectedVehicle.getFullInfo() : "Chưa chọn",
            selectedServices != null ? selectedServices.size() : 0);
            
        Toast.makeText(this, "Bước 6 - Xác nhận booking:\n" + summary, Toast.LENGTH_LONG).show();
    }
}
