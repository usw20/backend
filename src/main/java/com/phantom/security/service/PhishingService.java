package com.phantom.security.service;

import com.phantom.security.dto.request.PhishingScanRequest;
import com.phantom.security.dto.response.PhishingScanResponse;
import com.phantom.security.dto.response.PhishingScanResult;
import com.phantom.security.model.PhishingScanLog;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PhishingService {

    @Autowired
    private PhishingScanLogRepository phishingScanLogRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.phishing.server.url:http://localhost:5002}")
    private String aiPhishingServerUrl;

    /**
     * 피싱 스캔 데이터 처리 (전처리 + AI 호출 + 결과 저장)
     */
    public PhishingScanResult processScanData(String userId, PhishingScanRequest request) {
        try {
            // 1. 데이터 전처리
            Map<String, Object> preprocessedData = preprocessPhishingData(request, userId);

            // 2. AI 서버로 분석 요청
            Map<String, Object> aiResponse = callAiPhishingAnalysis(preprocessedData);

            // 3. 결과를 DB에 저장
            PhishingScanLog scanLog = createScanLogFromAiResponse(userId, request, aiResponse);
            phishingScanLogRepository.save(scanLog);

            // 4. 결과를 앱에 반환
            return createScanResult(aiResponse);

        } catch (Exception e) {
            // AI 서버 장애 시 폴백 로직
            return handleFallbackProcessing(userId, request);
        }
    }

    /**
     * 피싱 데이터 전처리
     */
    private Map<String, Object> preprocessPhishingData(PhishingScanRequest request, String userId) {
        Map<String, Object> data = new HashMap<>();

        // 기본 정보
        data.put("source_type", request.getSourceType());
        data.put("text_content", request.getTextContent());
        data.put("sender", request.getSender());
        data.put("user_id", userId);
        data.put("device_id", request.getDeviceId());

        // 텍스트 정규화 및 정제
        String cleanedText = cleanAndNormalizeText(request.getTextContent());
        data.put("cleaned_text", cleanedText);

        // URL 추출 및 분석
        List<String> extractedUrls = extractUrls(request.getTextContent());
        data.put("extracted_urls", extractedUrls);
        data.put("url_count", extractedUrls.size());
        data.put("suspicious_url_count", countSuspiciousUrls(extractedUrls));

        // 언어적 특징 추출
        Map<String, Double> languageFeatures = extractLanguageFeatures(cleanedText);
        data.put("language_features", languageFeatures);

        // 긴급성 점수
        data.put("urgency_score", calculateUrgencyScore(cleanedText));

        // 발신자 분석
        if (request.getSender() != null) {
            data.put("sender_analysis", analyzeSender(request.getSender()));
        }

        // 이메일 제목 분석 (이메일인 경우)
        if ("email".equals(request.getSourceType()) && request.getSubject() != null) {
            data.put("subject_analysis", analyzeSubject(request.getSubject()));
        }

        // 시간 기반 특징
        data.put("timestamp", request.getTimestamp() != null ?
                request.getTimestamp().toString() : LocalDateTime.now().toString());

        return data;
    }

    /**
     * AI 서버 호출
     */
    private Map<String, Object> callAiPhishingAnalysis(Map<String, Object> data) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    aiPhishingServerUrl + "/api/analyze/phishing",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return response.getBody();

        } catch (ResourceAccessException e) {
            throw new RuntimeException("AI 서버에 연결할 수 없습니다: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("AI 서버 요청 처리 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * AI 응답을 기반으로 스캔 로그 생성
     */
    private PhishingScanLog createScanLogFromAiResponse(String userId, PhishingScanRequest request, Map<String, Object> aiResponse) {
        PhishingScanLog scanLog = new PhishingScanLog();
        scanLog.setUserId(userId);
        scanLog.setDeviceId(request.getDeviceId());
        scanLog.setSourceType(request.getSourceType());
        scanLog.setTextContent(request.getTextContent());
        scanLog.setSender(request.getSender());

        // AI 분석 결과 적용
        Boolean isPhishing = (Boolean) aiResponse.get("is_phishing");
        Double confidence = ((Number) aiResponse.get("confidence")).doubleValue();

        scanLog.setScanResult(isPhishing != null && isPhishing ? "phishing" : "safe");
        scanLog.setConfidenceScore(confidence);
        scanLog.setPhishingType((String) aiResponse.get("phishing_type"));

        // 의심스러운 URL 추출
        @SuppressWarnings("unchecked")
        List<String> suspiciousUrls = (List<String>) aiResponse.get("suspicious_urls");
        if (suspiciousUrls != null && !suspiciousUrls.isEmpty()) {
            scanLog.setSuspiciousUrl(String.join(", ", suspiciousUrls));
        }

        scanLog.setDetectedAt(LocalDateTime.now());

        return scanLog;
    }

    /**
     * AI 응답을 앱 응답 형태로 변환
     */
    private PhishingScanResult createScanResult(Map<String, Object> aiResponse) {
        Boolean isPhishing = (Boolean) aiResponse.get("is_phishing");
        Double confidence = ((Number) aiResponse.get("confidence")).doubleValue();
        String phishingType = (String) aiResponse.get("phishing_type");

        PhishingScanResult result = new PhishingScanResult(isPhishing, confidence, phishingType);

        @SuppressWarnings("unchecked")
        List<String> riskIndicators = (List<String>) aiResponse.get("risk_indicators");
        if (riskIndicators != null) {
            result.setRiskIndicators(riskIndicators);
        }

        @SuppressWarnings("unchecked")
        List<String> suspiciousUrls = (List<String>) aiResponse.get("suspicious_urls");
        if (suspiciousUrls != null) {
            result.setSuspiciousUrls(suspiciousUrls);
        }

        return result;
    }

    /**
     * AI 서버 장애 시 폴백 탐지 로직
     */
    private PhishingScanResult handleFallbackProcessing(String userId, PhishingScanRequest request) {
        String text = request.getTextContent().toLowerCase();

        // 피싱 의심 키워드
        String[] phishingKeywords = {
                "긴급", "즉시", "확인", "계정", "정지", "suspended", "verify", "account",
                "링크", "클릭", "무료", "당첨", "쿠폰", "혜택", "환급", "세금", "환불",
                "비밀번호", "password", "login", "카드", "계좌", "입금", "송금"
        };

        // URL 패턴
        List<String> urls = extractUrls(request.getTextContent());

        boolean isPhishing = false;
        String phishingType = "unknown";
        double confidence = 0.3;
        List<String> indicators = new ArrayList<>();

        // 키워드 검사
        int suspiciousKeywords = 0;
        for (String keyword : phishingKeywords) {
            if (text.contains(keyword)) {
                suspiciousKeywords++;
                indicators.add("suspicious_keyword: " + keyword);
            }
        }

        // URL 검사
        if (!urls.isEmpty()) {
            indicators.add("contains_urls");
            if (urls.size() > 2) {
                indicators.add("multiple_urls");
                suspiciousKeywords++;
            }
        }

        // 판정 로직
        if (suspiciousKeywords >= 2) {
            isPhishing = true;
            confidence = Math.min(0.6 + (suspiciousKeywords * 0.1), 0.9);

            if (text.contains("계좌") || text.contains("카드") || text.contains("입금")) {
                phishingType = "financial";
            } else if (text.contains("개인정보") || text.contains("비밀번호")) {
                phishingType = "personal_info";
            } else {
                phishingType = "scam";
            }
        }

        // 폴백 결과도 DB에 저장
        PhishingScanLog scanLog = new PhishingScanLog(
                userId, request.getDeviceId(), request.getSourceType(), request.getTextContent()
        );
        scanLog.setSender(request.getSender());
        scanLog.setScanResult(isPhishing ? "phishing" : "safe");
        scanLog.setConfidenceScore(confidence);
        scanLog.setPhishingType(phishingType);
        if (!urls.isEmpty()) {
            scanLog.setSuspiciousUrl(String.join(", ", urls));
        }
        scanLog.setDetectedAt(LocalDateTime.now());

        phishingScanLogRepository.save(scanLog);

        PhishingScanResult result = new PhishingScanResult(isPhishing, confidence, phishingType);
        result.setRiskIndicators(indicators);
        result.setSuspiciousUrls(urls);

        return result;
    }

    /**
     * 사용자별 피싱 탐지 이력 조회
     */
    public List<PhishingScanResponse> getPhishingHistory(String userId) {
        List<PhishingScanLog> logs = phishingScanLogRepository.findByUserIdOrderByDetectedAtDesc(userId);

        return logs.stream()
                .map(PhishingScanResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 텍스트 정규화 및 정제
     */
    private String cleanAndNormalizeText(String text) {
        if (text == null) return "";

        return text.trim()
                .replaceAll("\\s+", " ")  // 연속 공백 제거
                .replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", ""); // 특수문자 정제
    }

    /**
     * URL 추출
     */
    private List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        Pattern urlPattern = Pattern.compile(
                "(?i)\\b(?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\([^\\s()<>]+\\))+",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }

    /**
     * 언어적 특징 추출
     */
    private Map<String, Double> extractLanguageFeatures(String text) {
        Map<String, Double> features = new HashMap<>();

        features.put("urgency_keywords", (double) countUrgencyKeywords(text));
        features.put("financial_keywords", (double) countFinancialKeywords(text));
        features.put("personal_info_requests", (double) countPersonalInfoRequests(text));
        features.put("click_inducement", (double) countClickInducement(text));
        features.put("text_length", (double) text.length());
        features.put("exclamation_count", (double) countCharacter(text, '!'));

        return features;
    }

    private int countUrgencyKeywords(String text) {
        String[] urgencyWords = {"긴급", "즉시", "바로", "지금", "urgent", "immediate"};
        return (int) Arrays.stream(urgencyWords)
                .mapToLong(word -> countOccurrences(text.toLowerCase(), word))
                .sum();
    }

    private int countFinancialKeywords(String text) {
        String[] financialWords = {"계좌", "카드", "입금", "출금", "송금", "환불", "세금", "bank", "account"};
        return (int) Arrays.stream(financialWords)
                .mapToLong(word -> countOccurrences(text.toLowerCase(), word))
                .sum();
    }

    private int countPersonalInfoRequests(String text) {
        String[] personalWords = {"비밀번호", "개인정보", "주민번호", "password", "personal", "verify"};
        return (int) Arrays.stream(personalWords)
                .mapToLong(word -> countOccurrences(text.toLowerCase(), word))
                .sum();
    }

    private int countClickInducement(String text) {
        String[] clickWords = {"클릭", "링크", "확인", "접속", "click", "link", "visit"};
        return (int) Arrays.stream(clickWords)
                .mapToLong(word -> countOccurrences(text.toLowerCase(), word))
                .sum();
    }

    private long countOccurrences(String text, String word) {
        return (text.length() - text.replace(word, "").length()) / word.length();
    }

    private long countCharacter(String text, char character) {
        return text.chars().filter(ch -> ch == character).count();
    }

    /**
     * 긴급성 점수 계산
     */
    private double calculateUrgencyScore(String text) {
        double score = 0.0;

        score += countUrgencyKeywords(text) * 0.3;
        score += countCharacter(text, '!') * 0.1;
        score += text.contains("24시간") || text.contains("오늘") ? 0.2 : 0.0;

        return Math.min(score, 1.0);
    }

    /**
     * 발신자 분석
     */
    private Map<String, Object> analyzeSender(String sender) {
        Map<String, Object> analysis = new HashMap<>();

        if (sender == null || sender.trim().isEmpty()) {
            analysis.put("is_suspicious", true);
            analysis.put("reason", "no_sender");
            return analysis;
        }

        // 숫자만으로 구성된 발신자 (스팸 가능성)
        boolean isNumericOnly = sender.matches("\\d+");
        analysis.put("is_numeric_only", isNumericOnly);

        // 랜덤 문자열 패턴
        boolean isRandomPattern = sender.matches("[a-zA-Z0-9]{8,}") &&
                !sender.toLowerCase().contains("noreply") &&
                !sender.toLowerCase().contains("admin");
        analysis.put("is_random_pattern", isRandomPattern);

        // 의심스러운 도메인
        if (sender.contains("@")) {
            String domain = sender.substring(sender.indexOf("@") + 1);
            boolean isSuspiciousDomain = checkSuspiciousDomain(domain);
            analysis.put("is_suspicious_domain", isSuspiciousDomain);
        }

        analysis.put("is_suspicious", isNumericOnly || isRandomPattern);

        return analysis;
    }

    /**
     * 이메일 제목 분석
     */
    private Map<String, Object> analyzeSubject(String subject) {
        Map<String, Object> analysis = new HashMap<>();

        if (subject == null || subject.trim().isEmpty()) {
            analysis.put("is_suspicious", true);
            analysis.put("reason", "no_subject");
            return analysis;
        }

        String lowerSubject = subject.toLowerCase();

        // 스팸 제목 패턴
        String[] spamPatterns = {
                "re:", "fwd:", "[광고]", "무료", "당첨", "긴급",
                "winner", "congratulations", "urgent", "free"
        };

        boolean hasSpamPattern = Arrays.stream(spamPatterns)
                .anyMatch(lowerSubject::contains);

        analysis.put("has_spam_pattern", hasSpamPattern);
        analysis.put("exclamation_count", countCharacter(subject, '!'));
        analysis.put("is_all_caps", subject.equals(subject.toUpperCase()) && subject.length() > 5);
        analysis.put("is_suspicious", hasSpamPattern || countCharacter(subject, '!') > 2);

        return analysis;
    }

    /**
     * 의심스러운 URL 개수 계산
     */
    private int countSuspiciousUrls(List<String> urls) {
        int suspiciousCount = 0;

        for (String url : urls) {
            if (isSuspiciousUrl(url)) {
                suspiciousCount++;
            }
        }

        return suspiciousCount;
    }

    /**
     * 의심스러운 URL 판별
     */
    private boolean isSuspiciousUrl(String url) {
        String lowerUrl = url.toLowerCase();

        // URL 단축 서비스
        String[] shorteners = {"bit.ly", "tinyurl", "t.co", "goo.gl", "ow.ly"};
        for (String shortener : shorteners) {
            if (lowerUrl.contains(shortener)) return true;
        }

        // 의심스러운 도메인 패턴
        if (lowerUrl.matches(".*[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}.*")) {
            return true; // IP 주소 사용
        }

        // 랜덤 서브도메인
        if (lowerUrl.matches(".*[a-z0-9]{8,}\\..*")) {
            return true;
        }

        return false;
    }

    /**
     * 의심스러운 도메인 체크
     */
    private boolean checkSuspiciousDomain(String domain) {
        String lowerDomain = domain.toLowerCase();

        // 알려진 안전한 도메인
        String[] safeDomains = {
                "gmail.com", "naver.com", "daum.net", "hanmail.net",
                "yahoo.com", "outlook.com", "hotmail.com"
        };

        for (String safeDomain : safeDomains) {
            if (lowerDomain.equals(safeDomain)) {
                return false;
            }
        }

        // 의심스러운 패턴
        return lowerDomain.matches(".*[0-9]{3,}.*") || // 숫자가 많이 포함
                lowerDomain.length() > 30 || // 너무 긴 도메인
                lowerDomain.split("\\.").length > 4; // 서브도메인이 너무 많음
    }

    /**
     * 피싱 탐지 통계
     */
    public Map<String, Long> getPhishingStatistics(String userId) {
        return Map.of(
                "total", phishingScanLogRepository.countByUserId(userId),
                "phishing", phishingScanLogRepository.countByUserIdAndScanResult(userId, "phishing"),
                "safe", phishingScanLogRepository.countByUserIdAndScanResult(userId, "safe"),
                "sms_scans", phishingScanLogRepository.countByUserIdAndSourceType(userId, "sms"),
                "email_scans", phishingScanLogRepository.countByUserIdAndSourceType(userId, "email")
        );
    }
}