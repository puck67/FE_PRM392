package com.example.prm.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServiceListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ServiceData data;

    @SerializedName("errors")
    private List<String> errors;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ServiceData getData() { return data; }
    public void setData(ServiceData data) { this.data = data; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public static class ServiceData {
        @SerializedName("services")
        private List<Service> services;

        @SerializedName("totalCount")
        private int totalCount;

        @SerializedName("pageNumber")
        private int pageNumber;

        @SerializedName("pageSize")
        private int pageSize;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("hasPreviousPage")
        private boolean hasPreviousPage;

        @SerializedName("hasNextPage")
        private boolean hasNextPage;

        // Getters and Setters
        public List<Service> getServices() { return services; }
        public void setServices(List<Service> services) { this.services = services; }

        // Legacy method for backward compatibility
        public List<Service> getItems() { return services; }
        public void setItems(List<Service> items) { this.services = items; }

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

        public int getPageNumber() { return pageNumber; }
        public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

        public boolean isHasPreviousPage() { return hasPreviousPage; }
        public void setHasPreviousPage(boolean hasPreviousPage) { this.hasPreviousPage = hasPreviousPage; }

        public boolean isHasNextPage() { return hasNextPage; }
        public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }
    }
}
