package com.example.prm.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AvailabilityResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AvailabilityData data;

    @SerializedName("errors")
    private List<String> errors;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public AvailabilityData getData() { return data; }
    public void setData(AvailabilityData data) { this.data = data; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public static class AvailabilityData {
        @SerializedName("date")
        private String date;

        @SerializedName("centerId")
        private int centerId;

        @SerializedName("availableSlots")
        private List<String> availableSlots;

        @SerializedName("totalSlots")
        private int totalSlots;

        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public int getCenterId() { return centerId; }
        public void setCenterId(int centerId) { this.centerId = centerId; }

        public List<String> getAvailableSlots() { return availableSlots; }
        public void setAvailableSlots(List<String> availableSlots) { this.availableSlots = availableSlots; }

        public int getTotalSlots() { return totalSlots; }
        public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }
    }
}
