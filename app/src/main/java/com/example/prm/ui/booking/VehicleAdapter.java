package com.example.prm.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;
import com.example.prm.models.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicles;
    private Vehicle selectedVehicle;
    private OnVehicleClickListener listener;

    public interface OnVehicleClickListener {
        void onVehicleSelect(Vehicle vehicle);
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
        this.selectedVehicle = null;
        notifyDataSetChanged();
    }

    public void addVehicles(List<Vehicle> newVehicles) {
        if (newVehicles != null && !newVehicles.isEmpty()) {
            int startPosition = this.vehicles.size();
            this.vehicles.addAll(newVehicles);
            notifyItemRangeInserted(startPosition, newVehicles.size());
        }
    }

    public Vehicle getSelectedVehicle() {
        return selectedVehicle;
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
        holder.bind(vehicle, vehicle.equals(selectedVehicle));
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVehicleName;
        private TextView tvLicensePlate;
        private TextView tvVehicleColor;
        private TextView tvEngineType;
        private ImageView ivSelectedIndicator;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            tvVehicleName = itemView.findViewById(R.id.tv_vehicle_name);
            tvLicensePlate = itemView.findViewById(R.id.tv_license_plate);
            tvVehicleColor = itemView.findViewById(R.id.tv_vehicle_color);
            tvEngineType = itemView.findViewById(R.id.tv_current_mileage);
            ivSelectedIndicator = itemView.findViewById(R.id.iv_selected_indicator);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Vehicle vehicle = vehicles.get(position);
                    selectedVehicle = vehicle;
                    notifyDataSetChanged();
                    
                    if (listener != null) {
                        listener.onVehicleSelect(vehicle);
                    }
                }
            });
        }

        public void bind(Vehicle vehicle, boolean isSelected) {
            // Set vehicle info
            tvVehicleName.setText(vehicle.getDisplayName());
            tvLicensePlate.setText(vehicle.getLicensePlate());
            tvVehicleColor.setText(vehicle.getColor() != null ? vehicle.getColor() : "Không rõ");
            
            // Format mileage display
            String mileageText = String.format("%,d km", vehicle.getCurrentMileage());
            tvEngineType.setText(mileageText);

            // Show/hide selection indicator
            ivSelectedIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // Update card background
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_vehicle_selected);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_vehicle_normal);
            }
        }
    }
}
