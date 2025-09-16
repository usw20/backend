package com.phantom.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class PhishingScanRequest {

    @NotBlank(message = "기기 ID는 필수 항목입니다.")
    private String deviceId;

    @NotBlank(message = "소스 타입은 필수 항목입니다.")
    private String sourceType; // "sms" or "email"

    @NotBlank(message = "텍스트 내용은 필수 항목입니다.")
    private String textContent;

    private String sender;
    private LocalDateTime timestamp;
    private List<String> extractedUrls;
    private String subject; // 이메일의 경우

    // Getter와 Setter
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getExtractedUrls() {
        return extractedUrls;
    }

    public void setExtractedUrls(List<String> extractedUrls) {
        this.extractedUrls = extractedUrls;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}