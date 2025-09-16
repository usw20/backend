package com.phantom.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatbotMessageRequest {

    @NotBlank(message = "메시지는 필수 항목입니다.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    private String message;

    private String conversationId; // 대화 세션 ID (선택사항)
    private String context; // 추가 컨텍스트 정보

    // Getter와 Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
