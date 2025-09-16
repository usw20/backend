package com.phantom.security.dto.response;

import java.time.LocalDateTime;

public class ChatbotMessageResponse {
    private String response;
    private String conversationId;
    private String category;
    private Double responseTime;
    private LocalDateTime timestamp;
    private Boolean requiresFollowup;
    private String suggestedActions; // 추천 액션

    public ChatbotMessageResponse(String response) {
        this.response = response;
        this.timestamp = LocalDateTime.now();
    }

    public ChatbotMessageResponse(String response, String conversationId, String category) {
        this.response = response;
        this.conversationId = conversationId;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }

    // Getter와 Setter
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRequiresFollowup() {
        return requiresFollowup;
    }

    public void setRequiresFollowup(Boolean requiresFollowup) {
        this.requiresFollowup = requiresFollowup;
    }

    public String getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(String suggestedActions) {
        this.suggestedActions = suggestedActions;
    }
}