package com.example.prm.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Center {
    @SerializedName("centerId")
    private int centerId;

    @SerializedName("centerName")
    private String centerName;

    @SerializedName("address")
    private String address;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("createdAt")
    private String createdAt;

    // Additional fields for UI only (not from API)
    private float rating = 0.0f;
    private int totalReviews = 0;
    private String city = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String operatingHours = "8:00 - 18:00";
    private String imageUrl = "";
    private String email = "";
    private String description = "";

    // Constructors
    public Center() {}

    public Center(int centerId, String centerName, String address, String phoneNumber, 
                 boolean isActive, String createdAt) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters - API fields
    public int getCenterId() { return centerId; }
    public void setCenterId(int centerId) { this.centerId = centerId; }

    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }

    // Legacy methods for backward compatibility
    public int getId() { return centerId; }
    public void setId(int id) { this.centerId = id; }

    public String getName() { return centerName; }
    public void setName(String name) { this.centerName = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Helper method để tính khoảng cách (tạm thời return 0)
    public double getDistanceFromUser() {
        return 0.0; // TODO: Implement distance calculation
    }

    // Helper method để format rating
    public String getFormattedRating() {
        return String.format("%.1f (%d đánh giá)", rating, totalReviews);
    }
}
