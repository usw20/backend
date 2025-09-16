package com.phantom.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatbotFeedbackRequest {

    @NotBlank(message = "메시지 ID는 필수 항목입니다.")
    private String messageId;

    @NotNull(message = "피드백은 필수 항목입니다.")
    private Boolean isHelpful;

    private String comment; // 추가 피드백 (선택사항)

    // Getter와 Setter
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Boolean getIsHelpful() {
        return isHelpful;
    }

    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}