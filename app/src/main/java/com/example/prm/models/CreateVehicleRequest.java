package com.example.prm.models;

import com.google.gson.annotations.SerializedName;

public class CreateVehicleRequest {
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
    private String lastServiceDate; // Format: "YYYY-MM-DD" hoặc null

    // Constructors
    public CreateVehicleRequest() {}

    public CreateVehicleRequest(int customerId, String vin, String licensePlate, String color, int currentMileage, String lastServiceDate) {
        this.customerId = customerId;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.color = color;
        this.currentMileage = currentMileage;
        this.lastServiceDate = lastServiceDate;
    }

    // Getters and Setters
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

    
}
