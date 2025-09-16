package com.phantom.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "phishing_scan_logs")
public class PhishingScanLog {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String deviceId;

    private String sourceType; // "sms" or "email"

    private String textContent;

    private String suspiciousUrl;

    private String scanResult; // "safe" or "phishing"

    @Indexed
    private LocalDateTime detectedAt = LocalDateTime.now();

    // 추가 분석 정보
    private String sender;
    private Double confidenceScore;
    private String phishingType; // "financial", "personal_info", "malware", "scam"

    // 기본 생성자
    public PhishingScanLog() {}

    // 생성자
    public PhishingScanLog(String userId, String deviceId, String sourceType, String textContent) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.sourceType = sourceType;
        this.textContent = textContent;
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getSuspiciousUrl() {
        return suspiciousUrl;
    }

    public void setSuspiciousUrl(String suspiciousUrl) {
        this.suspiciousUrl = suspiciousUrl;
    }

    public String getScanResult() {
        return scanResult;
    }

    public void setScanResult(String scanResult) {
        this.scanResult = scanResult;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getPhishingType() {
        return phishingType;
    }

    public void setPhishingType(String phishingType) {
        this.phishingType = phishingType;
    }
}