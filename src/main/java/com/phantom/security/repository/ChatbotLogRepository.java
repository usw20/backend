package com.phantom.security.repository;

import com.phantom.security.model.ChatbotLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotLogRepository extends MongoRepository<ChatbotLog, String> {

    /**
     * 사용자별 대화 기록 조회 (최신 순)
     */
    List<ChatbotLog> findByUserIdOrderBySentAtDesc(String userId);

    /**
     * 사용자별 대화 기록 조회 (페이징)
     */
    List<ChatbotLog> findByUserId(String userId, Sort sort);

    /**
     * 특정 대화 세션의 기록 조회
     */
    List<ChatbotLog> findByUserIdAndConversationIdOrderBySentAtAsc(String userId, String conversationId);

    /**
     * 사용자별 특정 기간 내 대화 기록 조회
     */
    List<ChatbotLog> findByUserIdAndSentAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 사용자별 특정 카테고리 대화 기록 조회
     */
    List<ChatbotLog> findByUserIdAndCategory(String userId, String category);

    /**
     * 사용자별 최근 N개 대화 조회
     */
    List<ChatbotLog> findTop10ByUserIdOrderBySentAtDesc(String userId);

    /**
     * 특정 메시지 ID로 조회 (피드백용)
     */
    Optional<ChatbotLog> findByIdAndUserId(String id, String userId);

    /**
     * 사용자별 전체 대화 건수 조회
     */
    long countByUserId(String userId);

    /**
     * 사용자별 카테고리별 대화 건수 조회
     */
    long countByUserIdAndCategory(String userId, String category);

    /**
     * 사용자별 도움된 응답 수 조회
     */
    long countByUserIdAndIsHelpful(String userId, Boolean isHelpful);
}