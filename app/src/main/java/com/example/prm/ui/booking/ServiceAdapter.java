package com.example.prm.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;
import com.example.prm.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> services;
    private OnServiceSelectionChangedListener listener;

    public interface OnServiceSelectionChangedListener {
        void onServiceSelectionChanged(List<Service> selectedServices, double totalPrice, int totalDuration);
    }

    public ServiceAdapter() {
        this.services = new ArrayList<>();
    }

    public void setOnServiceSelectionChangedListener(OnServiceSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void updateServices(List<Service> newServices) {
        this.services.clear();
        if (newServices != null) {
            this.services.addAll(newServices);
        }
        notifyDataSetChanged();
    }

    public void addServices(List<Service> newServices) {
        if (newServices != null && !newServices.isEmpty()) {
            int startPosition = this.services.size();
            this.services.addAll(newServices);
            notifyItemRangeInserted(startPosition, newServices.size());
        }
    }

    public List<Service> getSelectedServices() {
        List<Service> selectedServices = new ArrayList<>();
        for (Service service : services) {
            if (service.isSelected()) {
                selectedServices.add(service);
            }
        }
        return selectedServices;
    }

    public double getTotalPrice() {
        double total = 0;
        for (Service service : services) {
            if (service.isSelected()) {
                total += service.getBasePrice();
            }
        }
        return total;
    }

    public int getTotalDuration() {
        int total = 0;
        for (Service service : services) {
            if (service.isSelected()) {
                total += service.getEstimatedDuration();
            }
        }
        return total;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbServiceSelect;
        private ImageView ivServiceImage;
        private TextView tvServiceName;
        private TextView tvServiceDescription;
        private TextView tvServicePrice;
        private TextView tvServiceDuration;
        private TextView tvServiceCategory;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            cbServiceSelect = itemView.findViewById(R.id.cb_service_select);
            ivServiceImage = itemView.findViewById(R.id.iv_service_image);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDescription = itemView.findViewById(R.id.tv_service_description);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceDuration = itemView.findViewById(R.id.tv_service_duration);
            tvServiceCategory = itemView.findViewById(R.id.tv_service_category);
        }

        private void setupClickListeners() {
            // Click on card or checkbox
            View.OnClickListener clickListener = v -> toggleSelection();
            
            itemView.setOnClickListener(clickListener);
            cbServiceSelect.setOnClickListener(clickListener);
        }

        private void toggleSelection() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Service service = services.get(position);
                service.setSelected(!service.isSelected());
                cbServiceSelect.setChecked(service.isSelected());
                
                // Notify listener about selection change
                if (listener != null) {
                    listener.onServiceSelectionChanged(
                        getSelectedServices(), 
                        getTotalPrice(), 
                        getTotalDuration()
                    );
                }
            }
        }

        public void bind(Service service) {
            // Set basic info
            tvServiceName.setText(service.getServiceName());
            tvServiceDescription.setText(service.getDescription());
            tvServicePrice.setText(service.getFormattedPrice());
            tvServiceDuration.setText(service.getFormattedDuration());

            // Set checkbox state
            cbServiceSelect.setChecked(service.isSelected());

            // Set category if available
            if (service.getCategoryName() != null && !service.getCategoryName().isEmpty()) {
                tvServiceCategory.setText(service.getCategoryName());
                tvServiceCategory.setVisibility(View.VISIBLE);
            } else {
                tvServiceCategory.setVisibility(View.GONE);
            }

            // Load image (TODO: implement with Glide or Picasso)
            loadServiceImage(service.getImageUrl());
        }

        private void loadServiceImage(String imageUrl) {
            // TODO: Implement image loading with Glide
            // For now, set placeholder
            ivServiceImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}
