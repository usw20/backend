package com.phantom.security.controller;

import com.phantom.security.dto.request.PhishingScanRequest;
import com.phantom.security.dto.response.PhishingScanResponse;
import com.phantom.security.dto.response.PhishingScanResult;
import com.phantom.security.service.PhishingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/phishing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PhishingController {

    @Autowired
    private PhishingService phishingService;

    /**
     * 피싱 스캔 데이터 전송 및 즉시 결과 반환
     * POST /phishing/scan (실제 URL: /api/phishing/scan)
     */
    @PostMapping("/scan")
    public ResponseEntity<PhishingScanResult> submitScanData(
            @Valid @RequestBody PhishingScanRequest request,
            Authentication authentication) {

        String userId = authentication.getName();

        // 스캔 처리 및 즉시 결과 반환
        PhishingScanResult result = phishingService.processScanData(userId, request);

        return ResponseEntity.ok(result);
    }

    /**
     * 피싱 탐지 이력 조회
     * GET /phishing/history (실제 URL: /api/phishing/history)
     */
    @GetMapping("/history")
    public ResponseEntity<List<PhishingScanResponse>> getPhishingHistory(Authentication authentication) {
        String userId = authentication.getName();
        List<PhishingScanResponse> history = phishingService.getPhishingHistory(userId);

        return ResponseEntity.ok(history);
    }

    /**
     * 피싱 탐지 통계 조회
     * GET /phishing/statistics (실제 URL: /api/phishing/statistics)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getPhishingStatistics(Authentication authentication) {
        String userId = authentication.getName();
        Map<String, Long> statistics = phishingService.getPhishingStatistics(userId);

        return ResponseEntity.ok(statistics);
    }
}