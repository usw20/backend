package com.phantom.security.dto.response;

import com.phantom.security.model.User;

public class UserProfileResponse {
    private String id;
    private String email;
    private String phoneNumber;
    private Boolean isMalwareDetectionEnabled;
    private Boolean isPhishingDetectionEnabled;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.isMalwareDetectionEnabled = user.getIsMalwareDetectionEnabled();
        this.isPhishingDetectionEnabled = user.getIsPhishingDetectionEnabled();
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsMalwareDetectionEnabled() {
        return isMalwareDetectionEnabled;
    }

    public void setIsMalwareDetectionEnabled(Boolean isMalwareDetectionEnabled) {
        this.isMalwareDetectionEnabled = isMalwareDetectionEnabled;
    }

    public Boolean getIsPhishingDetectionEnabled() {
        return isPhishingDetectionEnabled;
    }

    public void setIsPhishingDetectionEnabled(Boolean isPhishingDetectionEnabled) {
        this.isPhishingDetectionEnabled = isPhishingDetectionEnabled;
    }
}