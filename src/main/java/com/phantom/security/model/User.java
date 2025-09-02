package com.phantom.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private String phoneNumber;

    private Boolean isMalwareDetectionEnabled = true;

    private Boolean isPhishingDetectionEnabled = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // 기본 생성자
    public User() {}

    // 생성자
    public User(String email, String passwordHash, String phoneNumber) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}