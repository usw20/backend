package com.phantom.security.controller;

import com.phantom.security.dto.request.ChangePasswordRequest;
import com.phantom.security.dto.request.SecuritySettingRequest;
import com.phantom.security.dto.request.UpdateProfileRequest;
import com.phantom.security.dto.response.UserProfileResponse;
import com.phantom.security.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 사용자 프로필 조회
     * GET /user/profile (실제 URL: /api/user/profile)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String userId = authentication.getName();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 프로필 정보 수정
     * PUT /user/profile (실제 URL: /api/user/profile)
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        UserProfileResponse updatedProfile = userService.updateProfile(userId, request);

        return ResponseEntity.ok(Map.of(
                "message", "프로필 정보가 업데이트되었습니다.",
                "user", updatedProfile
        ));
    }

    /**
     * 비밀번호 변경
     * PUT /auth/change-password (실제 URL: /api/auth/change-password)
     */
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        String message = userService.changePassword(userId, request);

        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * 악성코드 탐지 기능 설정
     * PUT /user/settings/malware (실제 URL: /api/user/settings/malware)
     */
    @PutMapping("/settings/malware")
    public ResponseEntity<Map<String, Object>> updateMalwareDetectionSetting(
            @Valid @RequestBody SecuritySettingRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        String message = userService.updateMalwareDetectionSetting(userId, request.getIsEnabled());

        return ResponseEntity.ok(Map.of(
                "message", message,
                "isEnabled", request.getIsEnabled()
        ));
    }

    /**
     * 피싱 탐지 기능 설정
     * PUT /user/settings/phishing (실제 URL: /api/user/settings/phishing)
     */
    @PutMapping("/settings/phishing")
    public ResponseEntity<Map<String, Object>> updatePhishingDetectionSetting(
            @Valid @RequestBody SecuritySettingRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        String message = userService.updatePhishingDetectionSetting(userId, request.getIsEnabled());

        return ResponseEntity.ok(Map.of(
                "message", message,
                "isEnabled", request.getIsEnabled()
        ));
    }

    /**
     * 계정 삭제
     * DELETE /user/delete (실제 URL: /api/user/delete)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteAccount(Authentication authentication) {
        String userId = authentication.getName();
        String message = userService.deleteAccount(userId);

        return ResponseEntity.ok(Map.of("message", message));
    }
}