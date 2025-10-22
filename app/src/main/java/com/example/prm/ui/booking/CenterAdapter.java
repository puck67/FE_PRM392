package com.example.prm.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;
import com.example.prm.models.Center;

import java.util.ArrayList;
import java.util.List;

public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.CenterViewHolder> {

    private List<Center> centers;
    private OnCenterClickListener listener;

    public interface OnCenterClickListener {
        void onCenterSelect(Center center);
        void onCenterDetails(Center center);
    }

    public CenterAdapter() {
        this.centers = new ArrayList<>();
    }

    public void setOnCenterClickListener(OnCenterClickListener listener) {
        this.listener = listener;
    }

    public void updateCenters(List<Center> newCenters) {
        this.centers.clear();
        if (newCenters != null) {
            this.centers.addAll(newCenters);
        }
        notifyDataSetChanged();
    }

    public void addCenters(List<Center> newCenters) {
        if (newCenters != null && !newCenters.isEmpty()) {
            int startPosition = this.centers.size();
            this.centers.addAll(newCenters);
            notifyItemRangeInserted(startPosition, newCenters.size());
        }
    }

    @NonNull
    @Override
    public CenterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_center, parent, false);
        return new CenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CenterViewHolder holder, int position) {
        Center center = centers.get(position);
        holder.bind(center);
    }

    @Override
    public int getItemCount() {
        return centers.size();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCenterImage;
        private TextView tvCenterName;
        private TextView tvCenterAddress;
        private RatingBar ratingCenter;
        private TextView tvRatingText;
        private TextView tvPhone;
        private TextView tvDistance;
        private TextView tvOperatingHours;
        private Button btnSelectCenter;

        public CenterViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            ivCenterImage = itemView.findViewById(R.id.iv_center_image);
            tvCenterName = itemView.findViewById(R.id.tv_center_name);
            tvCenterAddress = itemView.findViewById(R.id.tv_center_address);
            ratingCenter = itemView.findViewById(R.id.rating_center);
            tvRatingText = itemView.findViewById(R.id.tv_rating_text);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvOperatingHours = itemView.findViewById(R.id.tv_operating_hours);
            btnSelectCenter = itemView.findViewById(R.id.btn_select_center);
        }

        private void setupClickListeners() {
            // Click trên card để xem chi tiết
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCenterDetails(centers.get(getAdapterPosition()));
                }
            });

            // Click button chọn trung tâm
            btnSelectCenter.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCenterSelect(centers.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Center center) {
            // Set basic info
            tvCenterName.setText(center.getName());
            tvCenterAddress.setText(center.getAddress());
            tvPhone.setText(center.getPhoneNumber());

            // Set rating
            ratingCenter.setRating(center.getRating());
            tvRatingText.setText(center.getFormattedRating());

            // Set distance (tạm thời hardcode)
            tvDistance.setText(String.format("%.1f km", center.getDistanceFromUser()));

            // Set operating hours và status
            String operatingStatus = getOperatingStatus(center);
            tvOperatingHours.setText(center.getOperatingHours() + " • " + operatingStatus);

            // Set status color
            int statusColor = center.isActive() ? 
                itemView.getContext().getColor(R.color.success_color) :
                itemView.getContext().getColor(R.color.error_color);
            tvOperatingHours.setTextColor(statusColor);

            // Load image (TODO: implement with Glide or Picasso)
            loadCenterImage(center.getImageUrl());

            // Enable/disable select button
            btnSelectCenter.setEnabled(center.isActive());
            btnSelectCenter.setAlpha(center.isActive() ? 1.0f : 0.5f);
        }

        private String getOperatingStatus(Center center) {
            if (!center.isActive()) {
                return "Tạm đóng cửa";
            }
            
            // TODO: Implement real-time status check based on operating hours
            // For now, return "Đang mở"
            return "Đang mở";
        }

        private void loadCenterImage(String imageUrl) {
            // TODO: Implement image loading with Glide
            // For now, set placeholder
            ivCenterImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}
