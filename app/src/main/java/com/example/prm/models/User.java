package com.example.prm.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    @SerializedName("role")
    private String role;

    @SerializedName("emailVerified")
    private boolean emailVerified;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("lastLogin")
    private String lastLogin;

    // Constructors
    public User() {}

    public User(String fullName, String email, String phoneNumber, String dateOfBirth, 
               String gender, String address) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    // Helper methods
    public String getGenderDisplay() {
        if (gender != null) {
            switch (gender.toUpperCase()) {
                case "MALE": return "Nam";
                case "FEMALE": return "Nữ";
                default: return gender;
            }
        }
        return "Không rõ";
    }

    public String getRoleDisplay() {
        if (role != null) {
            switch (role.toUpperCase()) {
                case "CUSTOMER": return "Khách hàng";
                case "STAFF": return "Nhân viên";
                case "ADMIN": return "Quản trị viên";
                default: return role;
            }
        }
        return "Không rõ";
    }

    public String getFormattedDateOfBirth() {
        if (dateOfBirth != null && dateOfBirth.contains("-")) {
            try {
                String[] parts = dateOfBirth.split("-");
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            } catch (Exception e) {
                return dateOfBirth;
            }
        }
        return dateOfBirth;
    }
}
