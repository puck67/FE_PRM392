package com.example.prm.ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.prm.R;

public class CenterDetailActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_detail);
        
        // Get center info
        int centerId = getIntent().getIntExtra("center_id", -1);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết trung tâm");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Show center info
        TextView tvCenterInfo = findViewById(R.id.tv_center_info);
        tvCenterInfo.setText("ID Trung tâm: " + centerId);
        
        Toast.makeText(this, "Xem chi tiết trung tâm ID: " + centerId, Toast.LENGTH_SHORT).show();
        
        // Back button
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}
