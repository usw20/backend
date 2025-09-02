package com.phantom.security.service;

import com.phantom.security.dto.request.ChangePasswordRequest;
import com.phantom.security.dto.request.UpdateProfileRequest;
import com.phantom.security.dto.response.UserProfileResponse;
import com.phantom.security.exception.InvalidCredentialsException;
import com.phantom.security.exception.UserNotFoundException;
import com.phantom.security.model.User;
import com.phantom.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 사용자 프로필 조회
     */
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return new UserProfileResponse(user);
    }

    /**
     * 프로필 정보 수정
     */
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return new UserProfileResponse(savedUser);
    }

    /**
     * 비밀번호 변경
     */
    public String changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호 설정
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "비밀번호가 성공적으로 변경되었습니다.";
    }

    /**
     * 악성코드 탐지 기능 설정
     */
    public String updateMalwareDetectionSetting(String userId, Boolean isEnabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.setIsMalwareDetectionEnabled(isEnabled);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "악성코드 탐지 기능 상태가 업데이트되었습니다.";
    }

    /**
     * 피싱 탐지 기능 설정
     */
    public String updatePhishingDetectionSetting(String userId, Boolean isEnabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.setIsPhishingDetectionEnabled(isEnabled);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "피싱 탐지 기능 상태가 업데이트되었습니다.";
    }

    /**
     * 계정 삭제
     */
    public String deleteAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);

        return "계정이 성공적으로 삭제되었습니다.";
    }
}