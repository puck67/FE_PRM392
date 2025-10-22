package com.example.prm.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Service implements Serializable {
    @SerializedName("serviceId")
    private int serviceId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("description")
    private String description;

    @SerializedName("basePrice")
    private double basePrice;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("createdAt")
    private String createdAt;

    // Additional fields for UI only (not from API)
    private int estimatedDuration = 60; // default 60 minutes
    private int categoryId = 0;
    private String categoryName = "";
    private String imageUrl = "";
    
    // For booking selection
    private boolean isSelected = false;

    // Constructors
    public Service() {}

    public Service(int serviceId, String serviceName, String description, double basePrice, 
                  boolean isActive, String createdAt) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.basePrice = basePrice;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters - API fields
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    // Legacy methods for backward compatibility
    public int getId() { return serviceId; }
    public void setId(int id) { this.serviceId = id; }

    public String getName() { return serviceName; }
    public void setName(String name) { this.serviceName = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Legacy price method
    public double getPrice() { return basePrice; }
    public void setPrice(double price) { this.basePrice = price; }

    public int getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("%,.0f VNĐ", basePrice);
    }

    public String getFormattedDuration() {
        int hours = estimatedDuration / 60;
        int minutes = estimatedDuration % 60;
        
        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "p";
        } else if (hours > 0) {
            return hours + " giờ";
        } else {
            return minutes + " phút";
        }
    }
}
