package com.example.prm.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm.R;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<String> timeSlots;
    private String selectedTimeSlot;
    private OnTimeSlotClickListener listener;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(String timeSlot, boolean isSelected);
    }

    public TimeSlotAdapter() {
        this.timeSlots = new ArrayList<>();
    }

    public void setOnTimeSlotClickListener(OnTimeSlotClickListener listener) {
        this.listener = listener;
    }

    public void updateTimeSlots(List<String> newTimeSlots) {
        this.timeSlots.clear();
        if (newTimeSlots != null) {
            this.timeSlots.addAll(newTimeSlots);
        }
        this.selectedTimeSlot = null;
        notifyDataSetChanged();
    }

    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        String timeSlot = timeSlots.get(position);
        holder.bind(timeSlot, timeSlot.equals(selectedTimeSlot));
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTimeSlot;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String timeSlot = timeSlots.get(position);
                    
                    // Toggle selection
                    if (timeSlot.equals(selectedTimeSlot)) {
                        selectedTimeSlot = null;
                    } else {
                        selectedTimeSlot = timeSlot;
                    }
                    
                    notifyDataSetChanged();
                    
                    if (listener != null) {
                        listener.onTimeSlotClick(timeSlot, timeSlot.equals(selectedTimeSlot));
                    }
                }
            });
        }

        public void bind(String timeSlot, boolean isSelected) {
            tvTimeSlot.setText(timeSlot);
            
            if (isSelected) {
                // Selected state
                itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_time_slot_selected));
                tvTimeSlot.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
            } else {
                // Normal state
                itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_time_slot_normal));
                tvTimeSlot.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_color));
            }
        }
    }
}
