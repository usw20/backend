package com.phantom.security.service;

import com.phantom.security.dto.request.LoginRequest;
import com.phantom.security.dto.request.SignupRequest;
import com.phantom.security.dto.response.AuthResponse;
import com.phantom.security.exception.UserAlreadyExistsException;
import com.phantom.security.exception.InvalidCredentialsException;
import com.phantom.security.model.User;
import com.phantom.security.repository.UserRepository;
import com.phantom.security.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    public AuthResponse signup(SignupRequest signupRequest) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("이미 등록된 이메일 주소입니다.");
        }

        // 새 사용자 생성
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setIsMalwareDetectionEnabled(true);
        user.setIsPhishingDetectionEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 사용자 저장
        User savedUser = userRepository.save(user);

        // JWT 토큰 생성
        String token = jwtUtil.generateJwtToken(savedUser.getId(), savedUser.getEmail());

        return new AuthResponse("회원가입이 성공적으로 완료되었습니다.", token, savedUser);
    }

    /**
     * 로그인
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // 사용자 조회
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userOptional.get();

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateJwtToken(user.getId(), user.getEmail());

        return new AuthResponse("로그인 성공", token, user);
    }

    /**
     * 아이디 찾기 (전화번호로)
     */
    public String findIdByPhoneNumber(String phoneNumber) {
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("해당 정보로 등록된 사용자가 없습니다.");
        }

        // 실제로는 여기서 이메일을 SMS나 다른 방법으로 전송해야 함
        return "등록된 이메일 주소를 전송했습니다.";
    }

    /**
     * 사용자 ID로 사용자 조회
     */
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }
}