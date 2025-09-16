package com.phantom.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "chatbot_logs")
public class ChatbotLog {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String userMessage;

    private String chatbotResponse;

    @Indexed
    private LocalDateTime sentAt = LocalDateTime.now();

    // 추가 메타데이터
    private String conversationId; // 대화 세션 ID
    private String messageType; // "question", "followup", "greeting"
    private String category; // "security_status", "threat_info", "general"
    private Double responseTime; // AI 서버 응답 시간 (초)
    private Boolean isHelpful; // 사용자 피드백

    // 기본 생성자
    public ChatbotLog() {}

    // 생성자
    public ChatbotLog(String userId, String userMessage, String chatbotResponse) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.chatbotResponse = chatbotResponse;
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

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
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

    public Double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }

    public Boolean getIsHelpful() {
        return isHelpful;
    }

    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }
}