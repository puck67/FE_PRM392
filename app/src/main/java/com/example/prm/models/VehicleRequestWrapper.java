package com.example.prm.models;

import com.google.gson.annotations.SerializedName;

public class VehicleRequestWrapper {
    @SerializedName("request")
    private CreateVehicleRequest request;

    public VehicleRequestWrapper() {}

    public VehicleRequestWrapper(CreateVehicleRequest request) {
        this.request = request;
    }

    public CreateVehicleRequest getRequest() { return request; }
    public void setRequest(CreateVehicleRequest request) { this.request = request; }
}
