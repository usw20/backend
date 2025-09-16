package com.phantom.security.controller;

import com.phantom.security.dto.request.ChatbotFeedbackRequest;
import com.phantom.security.dto.request.ChatbotMessageRequest;
import com.phantom.security.dto.response.ChatbotConversationResponse;
import com.phantom.security.dto.response.ChatbotMessageResponse;
import com.phantom.security.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * 챗봇에 메시지 전송 및 응답 받기
     * POST /chatbot/message (실제 URL: /api/chatbot/message)
     */
    @PostMapping("/message")
    public ResponseEntity<ChatbotMessageResponse> sendMessage(
            @Valid @RequestBody ChatbotMessageRequest request,
            Authentication authentication) {

        String userId = authentication.getName();

        // 챗봇 메시지 처리 및 응답 생성
        ChatbotMessageResponse response = chatbotService.processMessage(userId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 대화 기록 조회
     * GET /chatbot/history (실제 URL: /api/chatbot/history)
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatbotConversationResponse>> getChatHistory(Authentication authentication) {
        String userId = authentication.getName();
        List<ChatbotConversationResponse> history = chatbotService.getChatHistory(userId);

        return ResponseEntity.ok(history);
    }

    /**
     * 챗봇 응답에 대한 피드백 제출
     * POST /chatbot/feedback (실제 URL: /api/chatbot/feedback)
     */
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> submitFeedback(
            @Valid @RequestBody ChatbotFeedbackRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        String message = chatbotService.processFeedback(
                userId, request.getMessageId(), request.getIsHelpful(), request.getComment()
        );

        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * 챗봇 통계 조회
     * GET /chatbot/statistics (실제 URL: /api/chatbot/statistics)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getChatbotStatistics(Authentication authentication) {
        String userId = authentication.getName();
        Map<String, Long> statistics = chatbotService.getChatbotStatistics(userId);

        return ResponseEntity.ok(statistics);
    }
}