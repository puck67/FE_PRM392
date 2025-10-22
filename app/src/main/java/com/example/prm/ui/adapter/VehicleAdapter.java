package com.example.prm.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm.R;
import com.example.prm.models.Vehicle;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    
    private List<Vehicle> vehicles;
    private OnVehicleClickListener listener;
    
    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
    }
    
    public VehicleAdapter() {
        this.vehicles = new ArrayList<>();
    }
    
    public void setOnVehicleClickListener(OnVehicleClickListener listener) {
        this.listener = listener;
    }
    
    public void updateVehicles(List<Vehicle> newVehicles) {
        this.vehicles.clear();
        if (newVehicles != null) {
            this.vehicles.addAll(newVehicles);
        }
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.bind(vehicle);
    }
    
    @Override
    public int getItemCount() {
        return vehicles.size();
    }
    
    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvVehicleName;
        private TextView tvLicensePlate;
        private TextView tvVehicleColor;
        private TextView tvCurrentMileage;
        
        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleName = itemView.findViewById(R.id.tv_vehicle_name);
            tvLicensePlate = itemView.findViewById(R.id.tv_license_plate);
            tvVehicleColor = itemView.findViewById(R.id.tv_vehicle_color);
            tvCurrentMileage = itemView.findViewById(R.id.tv_current_mileage);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onVehicleClick(vehicles.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(Vehicle vehicle) {
            // Display VIN as vehicle name (since we don't have brand/model anymore)
            tvVehicleName.setText("VIN: " + vehicle.getVin());
            
            // License plate
            tvLicensePlate.setText(vehicle.getLicensePlate());
            
            // Color
            tvVehicleColor.setText(vehicle.getColor());
            
            // Current mileage with formatting
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            String mileageText = numberFormat.format(vehicle.getCurrentMileage()) + " km";
            tvCurrentMileage.setText(mileageText);
        }
    }
}
