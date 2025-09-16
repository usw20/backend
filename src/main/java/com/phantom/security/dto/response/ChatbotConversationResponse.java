package com.phantom.security.dto.response;

import com.phantom.security.model.ChatbotLog;
import java.time.LocalDateTime;

public class ChatbotConversationResponse {
    private String id;
    private String userMessage;
    private String chatbotResponse;
    private LocalDateTime sentAt;
    private String messageType;
    private String category;
    private Boolean isHelpful;

    public ChatbotConversationResponse(ChatbotLog log) {
        this.id = log.getId();
        this.userMessage = log.getUserMessage();
        this.chatbotResponse = log.getChatbotResponse();
        this.sentAt = log.getSentAt();
        this.messageType = log.getMessageType();
        this.category = log.getCategory();
        this.isHelpful = log.getIsHelpful();
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getChatbotResponse() {
        return chatbotResponse;
    }

    public void setChatbotResponse(String chatbotResponse) {
        this.chatbotResponse = chatbotResponse;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsHelpful() {
        return isHelpful;
    }

    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }
}