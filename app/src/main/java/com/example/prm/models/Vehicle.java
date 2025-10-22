package com.example.prm.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Vehicle implements Serializable {
    @SerializedName("vehicleId")
    private int vehicleId;

    @SerializedName("customerId")
    private int customerId;

    @SerializedName("vin")
    private String vin;

    @SerializedName("licensePlate")
    private String licensePlate;

    @SerializedName("color")
    private String color;

    @SerializedName("currentMileage")
    private int currentMileage;

    @SerializedName("lastServiceDate")
    private String lastServiceDate;

    @SerializedName("purchaseDate")
    private String purchaseDate;

    @SerializedName("nextServiceDue")
    private String nextServiceDue;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerPhone")
    private String customerPhone;

    // Additional fields for UI
    private boolean isSelected = false;

    // Constructors
    public Vehicle() {}

    public Vehicle(int vehicleId, int customerId, String vin, String licensePlate, 
                  String color, int currentMileage, String createdAt) {
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.color = color;
        this.currentMileage = currentMileage;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(int currentMileage) { this.currentMileage = currentMileage; }

    public String getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(String lastServiceDate) { this.lastServiceDate = lastServiceDate; }

    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getNextServiceDue() { return nextServiceDue; }
    public void setNextServiceDue(String nextServiceDue) { this.nextServiceDue = nextServiceDue; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    // Helper methods
    public String getDisplayName() {
        return "VIN: " + vin;
    }

    public String getFullInfo() {
        return getDisplayName() + " - " + licensePlate;
    }
}
