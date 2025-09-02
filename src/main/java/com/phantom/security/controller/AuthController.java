package com.phantom.security.controller;

import com.phantom.security.dto.request.LoginRequest;
import com.phantom.security.dto.request.SignupRequest;
import com.phantom.security.dto.response.AuthResponse;
import com.phantom.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth") // /api 제거 (컨텍스트 패스에서 자동 처리됨)
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 회원가입
     * POST /auth/signup (실제 URL: /api/auth/signup)
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AuthResponse response = authService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인
     * POST /auth/login (실제 URL: /api/auth/login)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 찾기 (전화번호로)
     * POST /auth/find-id (실제 URL: /api/auth/find-id)
     */
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findId(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String message = authService.findIdByPhoneNumber(phoneNumber);

        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * 비밀번호 재설정 링크 요청
     * POST /auth/forgot-password (실제 URL: /api/auth/forgot-password)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // TODO: 실제로는 이메일 발송 서비스를 구현해야 함
        // 현재는 단순히 성공 메시지만 반환

        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 링크가 이메일로 전송되었습니다."));
    }
}