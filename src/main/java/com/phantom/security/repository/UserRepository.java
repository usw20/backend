package com.phantom.security.repository;

import com.phantom.security.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 전화번호로 사용자 조회 (아이디 찾기용)
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 이메일 중복 확인
     */
    Boolean existsByEmail(String email);

    /**
     * 전화번호 중복 확인
     */
    Boolean existsByPhoneNumber(String phoneNumber);
}