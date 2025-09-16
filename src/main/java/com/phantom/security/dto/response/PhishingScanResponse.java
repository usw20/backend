package com.phantom.security.dto.response;

import com.phantom.security.model.PhishingScanLog;
import java.time.LocalDateTime;

public class PhishingScanResponse {
    private String id;
    private String sourceType;
    private String textContent;
    private String suspiciousUrl;
    private String scanResult;
    private LocalDateTime detectedAt;
    private String sender;
    private Double confidenceScore;
    private String phishingType;

    public PhishingScanResponse(PhishingScanLog log) {
        this.id = log.getId();
        this.sourceType = log.getSourceType();
        this.textContent = log.getTextContent();
        this.suspiciousUrl = log.getSuspiciousUrl();
        this.scanResult = log.getScanResult();
        this.detectedAt = log.getDetectedAt();
        this.sender = log.getSender();
        this.confidenceScore = log.getConfidenceScore();
        this.phishingType = log.getPhishingType();
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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