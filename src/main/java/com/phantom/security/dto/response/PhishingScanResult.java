package com.phantom.security.dto.response;

import java.util.List;

public class PhishingScanResult {
    private Boolean isPhishing;
    private Double confidence;
    private String phishingType;
    private String riskLevel;
    private List<String> riskIndicators;
    private List<String> suspiciousUrls;
    private Boolean shouldBlock;

    public PhishingScanResult(Boolean isPhishing, Double confidence, String phishingType) {
        this.isPhishing = isPhishing;
        this.confidence = confidence;
        this.phishingType = phishingType;
        this.riskLevel = calculateRiskLevel(confidence);
        this.shouldBlock = isPhishing && confidence > 0.7;
    }

    private String calculateRiskLevel(Double confidence) {
        if (confidence > 0.8) return "HIGH";
        if (confidence > 0.5) return "MEDIUM";
        return "LOW";
    }

    // Getter와 Setter
    public Boolean getIsPhishing() {
        return isPhishing;
    }

    public void setIsPhishing(Boolean isPhishing) {
        this.isPhishing = isPhishing;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getPhishingType() {
        return phishingType;
    }

    public void setPhishingType(String phishingType) {
        this.phishingType = phishingType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getRiskIndicators() {
        return riskIndicators;
    }

    public void setRiskIndicators(List<String> riskIndicators) {
        this.riskIndicators = riskIndicators;
    }

    public List<String> getSuspiciousUrls() {
        return suspiciousUrls;
    }

    public void setSuspiciousUrls(List<String> suspiciousUrls) {
        this.suspiciousUrls = suspiciousUrls;
    }

    public Boolean getShouldBlock() {
        return shouldBlock;
    }

    public void setShouldBlock(Boolean shouldBlock) {
        this.shouldBlock = shouldBlock;
    }
}