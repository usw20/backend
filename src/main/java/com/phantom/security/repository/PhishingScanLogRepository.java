package com.phantom.security.repository;

import com.phantom.security.model.PhishingScanLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhishingScanLogRepository extends MongoRepository<PhishingScanLog, String> {

    /**
     * 사용자별 피싱 탐지 이력 조회 (최신 순)
     */
    List<PhishingScanLog> findByUserIdOrderByDetectedAtDesc(String userId);

    /**
     * 사용자별 피싱 탐지 이력 조회 (페이징)
     */
    List<PhishingScanLog> findByUserId(String userId, Sort sort);

    /**
     * 사용자별 특정 기간 내 피싱 탐지 이력 조회
     */
    List<PhishingScanLog> findByUserIdAndDetectedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 사용자별 특정 스캔 결과만 조회
     */
    List<PhishingScanLog> findByUserIdAndScanResult(String userId, String scanResult);

    /**
     * 특정 소스 타입의 스캔 로그 조회
     */
    List<PhishingScanLog> findByUserIdAndSourceType(String userId, String sourceType);

    /**
     * 특정 기기의 스캔 로그 조회
     */
    List<PhishingScanLog> findByUserIdAndDeviceId(String userId, String deviceId);

    /**
     * 사용자별 피싱 탐지 건수 조회
     */
    long countByUserIdAndScanResult(String userId, String scanResult);

    /**
     * 사용자별 전체 스캔 건수 조회
     */
    long countByUserId(String userId);

    /**
     * 사용자별 소스 타입별 탐지 건수 조회
     */
    long countByUserIdAndSourceType(String userId, String sourceType);
}