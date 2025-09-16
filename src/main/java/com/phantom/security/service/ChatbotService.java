package com.phantom.security.service;

import com.phantom.security.dto.request.ChatbotMessageRequest;
import com.phantom.security.dto.response.ChatbotConversationResponse;
import com.phantom.security.dto.response.ChatbotMessageResponse;
import com.phantom.security.model.ChatbotLog;
import com.phantom.security.repository.ChatbotLogRepository;
import com.phantom.security.repository.MalwareScanLogRepository;
import com.phantom.security.repository.PhishingScanLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Autowired
    private ChatbotLogRepository chatbotLogRepository;

    @Autowired
    private MalwareScanLogRepository malwareScanLogRepository;

    @Autowired
    private PhishingScanLogRepository phishingScanLogRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.chatbot.server.url:http://localhost:5003}")
    private String aiChatbotServerUrl;

    /**
     * 챗봇 메시지 처리 (컨텍스트 분석 + AI 호출 + 응답 생성)
     */
    public ChatbotMessageResponse processMessage(String userId, ChatbotMessageRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 사용자 보안 컨텍스트 수집
            Map<String, Object> userContext = buildUserSecurityContext(userId);

            // 2. 대화 컨텍스트 분석
            Map<String, Object> conversationContext = buildConversationContext(userId, request);

            // 3. AI 챗봇 서버로 요청
            Map<String, Object> aiRequest = buildAiRequest(request, userContext, conversationContext);
            Map<String, Object> aiResponse = callAiChatbotServer(aiRequest);

            // 4. 응답 생성
            ChatbotMessageResponse response = createMessageResponse(aiResponse, request.getConversationId());

            // 5. 대화 기록 저장
            saveChatbotLog(userId, request, response, startTime);

            return response;

        } catch (Exception e) {
            // AI 서버 장애 시 폴백 응답
            return handleFallbackResponse(userId, request, startTime);
        }
    }

    /**
     * 사용자 보안 컨텍스트 수집
     */
    private Map<String, Object> buildUserSecurityContext(String userId) {
        Map<String, Object> context = new HashMap<>();

        // 악성코드 탐지 이력 요약
        long totalMalwareScans = malwareScanLogRepository.countByUserId(userId);
        long maliciousCount = malwareScanLogRepository.countByUserIdAndScanResult(userId, "malicious");
        long suspiciousCount = malwareScanLogRepository.countByUserIdAndScanResult(userId, "suspicious");

        context.put("malware_stats", Map.of(
                "total_scans", totalMalwareScans,
                "malicious_detected", maliciousCount,
                "suspicious_detected", suspiciousCount,
                "threat_level", calculateThreatLevel(maliciousCount, suspiciousCount, totalMalwareScans)
        ));

        // 피싱 탐지 이력 요약
        long totalPhishingScans = phishingScanLogRepository.countByUserId(userId);
        long phishingCount = phishingScanLogRepository.countByUserIdAndScanResult(userId, "phishing");
        long smsScans = phishingScanLogRepository.countByUserIdAndSourceType(userId, "sms");
        long emailScans = phishingScanLogRepository.countByUserIdAndSourceType(userId, "email");

        context.put("phishing_stats", Map.of(
                "total_scans", totalPhishingScans,
                "phishing_detected", phishingCount,
                "sms_scans", smsScans,
                "email_scans", emailScans
        ));

        // 최근 위협 활동 (지난 7일)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentMalware = malwareScanLogRepository.findByUserIdAndDetectedAtBetween(
                userId, weekAgo, LocalDateTime.now()).size();
        long recentPhishing = phishingScanLogRepository.findByUserIdAndDetectedAtBetween(
                userId, weekAgo, LocalDateTime.now()).size();

        context.put("recent_activity", Map.of(
                "malware_scans_last_7_days", recentMalware,
                "phishing_scans_last_7_days", recentPhishing
        ));

        return context;
    }

    /**
     * 대화 컨텍스트 분석
     */
    private Map<String, Object> buildConversationContext(String userId, ChatbotMessageRequest request) {
        Map<String, Object> context = new HashMap<>();

        // 메시지 분석
        String message = request.getMessage().toLowerCase();
        context.put("message_category", categorizeMessage(message));
        context.put("message_intent", analyzeIntent(message));
        context.put("requires_security_data", requiresSecurityData(message));

        // 대화 세션 컨텍스트
        if (request.getConversationId() != null) {
            List<ChatbotLog> recentConversation = chatbotLogRepository
                    .findByUserIdAndConversationIdOrderBySentAtAsc(userId, request.getConversationId());

            context.put("conversation_history", recentConversation.stream()
                    .map(log -> Map.of(
                            "user", log.getUserMessage(),
                            "bot", log.getChatbotResponse(),
                            "timestamp", log.getSentAt()
                    ))
                    .collect(Collectors.toList()));
        }

        // 최근 대화 패턴
        List<ChatbotLog> recentChats = chatbotLogRepository.findTop10ByUserIdOrderBySentAtDesc(userId);
        context.put("recent_topics", extractRecentTopics(recentChats));

        return context;
    }

    /**
     * AI 서버 요청 데이터 구성
     */
    private Map<String, Object> buildAiRequest(ChatbotMessageRequest request,
                                               Map<String, Object> userContext,
                                               Map<String, Object> conversationContext) {
        Map<String, Object> aiRequest = new HashMap<>();

        aiRequest.put("message", request.getMessage());
        aiRequest.put("conversation_id", request.getConversationId());
        aiRequest.put("user_context", userContext);
        aiRequest.put("conversation_context", conversationContext);
        aiRequest.put("timestamp", LocalDateTime.now().toString());

        return aiRequest;
    }

    /**
     * AI 챗봇 서버 호출
     */
    private Map<String, Object> callAiChatbotServer(Map<String, Object> request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    aiChatbotServerUrl + "/api/chat/message",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return response.getBody();

        } catch (ResourceAccessException e) {
            throw new RuntimeException("AI 챗봇 서버에 연결할 수 없습니다: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("AI 챗봇 서버 요청 처리 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 메시지 응답 생성
     */
    private ChatbotMessageResponse createMessageResponse(Map<String, Object> aiResponse, String conversationId) {
        String response = (String) aiResponse.get("response");
        String category = (String) aiResponse.get("category");

        ChatbotMessageResponse messageResponse = new ChatbotMessageResponse(response, conversationId, category);

        if (aiResponse.containsKey("response_time")) {
            messageResponse.setResponseTime(((Number) aiResponse.get("response_time")).doubleValue());
        }

        if (aiResponse.containsKey("requires_followup")) {
            messageResponse.setRequiresFollowup((Boolean) aiResponse.get("requires_followup"));
        }

        if (aiResponse.containsKey("suggested_actions")) {
            messageResponse.setSuggestedActions((String) aiResponse.get("suggested_actions"));
        }

        return messageResponse;
    }

    /**
     * 대화 기록 저장
     */
    private void saveChatbotLog(String userId, ChatbotMessageRequest request,
                                ChatbotMessageResponse response, long startTime) {
        ChatbotLog log = new ChatbotLog(userId, request.getMessage(), response.getResponse());

        log.setConversationId(response.getConversationId());
        log.setCategory(response.getCategory());
        log.setMessageType(determineMessageType(request.getMessage()));
        log.setResponseTime((System.currentTimeMillis() - startTime) / 1000.0);
        log.setSentAt(LocalDateTime.now());

        chatbotLogRepository.save(log);
    }

    /**
     * AI 서버 장애 시 폴백 응답
     */
    private ChatbotMessageResponse handleFallbackResponse(String userId, ChatbotMessageRequest request, long startTime) {
        String message = request.getMessage().toLowerCase();
        String response;
        String category = "general";

        if (message.contains("안전") || message.contains("보안") || message.contains("상태")) {
            response = generateSecurityStatusResponse(userId);
            category = "security_status";
        } else if (message.contains("악성코드") || message.contains("바이러스")) {
            response = "악성코드 관련 질문이시네요. 현재 시스템에서 실시간으로 악성코드를 탐지하고 있습니다. " +
                    "의심스러운 앱을 발견하면 즉시 알림을 드리니 안심하세요.";
            category = "malware_info";
        } else if (message.contains("피싱") || message.contains("스팸") || message.contains("문자")) {
            response = "피싱 메시지나 스팸에 대한 질문이시네요. " +
                    "의심스러운 링크는 절대 클릭하지 마시고, 개인정보를 요구하는 문자나 이메일은 주의하세요.";
            category = "phishing_info";
        } else if (message.contains("안녕") || message.contains("hello") || message.contains("hi")) {
            response = "안녕하세요! 저는 팬텀 보안 컨설턴트입니다. " +
                    "보안 관련 궁금한 점이 있으시면 언제든 물어보세요.";
            category = "greeting";
        } else {
            response = "죄송합니다. 현재 일시적으로 서비스에 문제가 있습니다. " +
                    "잠시 후 다시 시도해주세요. 긴급한 보안 문제가 있으시면 즉시 앱의 스캔 기능을 이용해주세요.";
        }

        ChatbotMessageResponse chatResponse = new ChatbotMessageResponse(response,
                UUID.randomUUID().toString(), category);
        chatResponse.setResponseTime((System.currentTimeMillis() - startTime) / 1000.0);

        // 폴백 응답도 기록 저장
        saveChatbotLog(userId, request, chatResponse, startTime);

        return chatResponse;
    }

    /**
     * 보안 상태 응답 생성
     */
    private String generateSecurityStatusResponse(String userId) {
        long maliciousCount = malwareScanLogRepository.countByUserIdAndScanResult(userId, "malicious");
        long phishingCount = phishingScanLogRepository.countByUserIdAndScanResult(userId, "phishing");

        if (maliciousCount == 0 && phishingCount == 0) {
            return "현재 보안 상태가 양호합니다! 악성코드나 피싱 위협이 탐지되지 않았습니다. " +
                    "앞으로도 안전한 인터넷 사용을 위해 주의해주세요.";
        } else {
            return String.format("보안 주의가 필요합니다. 악성코드 %d건, 피싱 %d건이 탐지되었습니다. " +
                            "탐지 이력을 확인하시고 필요시 추가 보안 조치를 취해주세요.",
                    maliciousCount, phishingCount);
        }
    }

    /**
     * 대화 기록 조회
     */
    public List<ChatbotConversationResponse> getChatHistory(String userId) {
        List<ChatbotLog> logs = chatbotLogRepository.findByUserIdOrderBySentAtDesc(userId);

        return logs.stream()
                .map(ChatbotConversationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 피드백 처리
     */
    public String processFeedback(String userId, String messageId, Boolean isHelpful, String comment) {
        Optional<ChatbotLog> logOptional = chatbotLogRepository.findByIdAndUserId(messageId, userId);

        if (logOptional.isPresent()) {
            ChatbotLog log = logOptional.get();
            log.setIsHelpful(isHelpful);
            chatbotLogRepository.save(log);

            return "피드백이 성공적으로 저장되었습니다.";
        } else {
            throw new RuntimeException("해당 메시지를 찾을 수 없습니다.");
        }
    }

    // 유틸리티 메서드들
    private String calculateThreatLevel(long malicious, long suspicious, long total) {
        if (total == 0) return "UNKNOWN";
        double riskRatio = (double) (malicious + suspicious) / total;

        if (riskRatio > 0.3) return "HIGH";
        if (riskRatio > 0.1) return "MEDIUM";
        return "LOW";
    }

    private String categorizeMessage(String message) {
        if (message.contains("안전") || message.contains("상태") || message.contains("보안")) {
            return "security_status";
        } else if (message.contains("악성코드") || message.contains("바이러스") || message.contains("malware")) {
            return "malware_info";
        } else if (message.contains("피싱") || message.contains("스팸") || message.contains("문자")) {
            return "phishing_info";
        } else if (message.contains("안녕") || message.contains("hello") || message.contains("hi")) {
            return "greeting";
        } else if (message.contains("도움") || message.contains("추천") || message.contains("어떻게")) {
            return "advice_request";
        } else {
            return "general";
        }
    }

    private String analyzeIntent(String message) {
        if (message.contains("?") || message.contains("어떻게") || message.contains("뭐") || message.contains("무엇")) {
            return "question";
        } else if (message.contains("고마") || message.contains("감사") || message.contains("thanks")) {
            return "appreciation";
        } else if (message.contains("문제") || message.contains("오류") || message.contains("안됨")) {
            return "problem_report";
        } else {
            return "statement";
        }
    }

    private boolean requiresSecurityData(String message) {
        String[] securityKeywords = {
                "안전", "보안", "상태", "탐지", "스캔", "이력", "기록", "통계"
        };

        return Arrays.stream(securityKeywords)
                .anyMatch(keyword -> message.contains(keyword));
    }

    private String determineMessageType(String message) {
        if (message.contains("안녕") || message.contains("hello") || message.contains("hi")) {
            return "greeting";
        } else if (message.contains("?") || message.contains("어떻게") || message.contains("뭐")) {
            return "question";
        } else {
            return "statement";
        }
    }

    private List<String> extractRecentTopics(List<ChatbotLog> recentChats) {
        return recentChats.stream()
                .map(ChatbotLog::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * 챗봇 통계
     */
    public Map<String, Long> getChatbotStatistics(String userId) {
        return Map.of(
                "total_conversations", chatbotLogRepository.countByUserId(userId),
                "helpful_responses", chatbotLogRepository.countByUserIdAndIsHelpful(userId, true),
                "security_questions", chatbotLogRepository.countByUserIdAndCategory(userId, "security_status"),
                "malware_questions", chatbotLogRepository.countByUserIdAndCategory(userId, "malware_info"),
                "phishing_questions", chatbotLogRepository.countByUserIdAndCategory(userId, "phishing_info")
        );
    }
}