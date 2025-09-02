package com.phantom.security.dto.response;

import com.phantom.security.model.User;

public class AuthResponse {
    private String message;
    private String token;
    private UserInfo user;

    public AuthResponse(String message, String token, User user) {
        this.message = message;
        this.token = token;
        this.user = new UserInfo(user);
    }

    // Getter와 Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    // 사용자 정보 내부 클래스 (비밀번호 제외)
    public static class UserInfo {
        private String id;
        private String email;
        private Boolean isMalwareDetectionEnabled;
        private Boolean isPhishingDetectionEnabled;

        public UserInfo(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.isMalwareDetectionEnabled = user.getIsMalwareDetectionEnabled();
            this.isPhishingDetectionEnabled = user.getIsPhishingDetectionEnabled();
        }

        // Getter와 Setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getIsMalwareDetectionEnabled() {
            return isMalwareDetectionEnabled;
        }

        public void setIsMalwareDetectionEnabled(Boolean isMalwareDetectionEnabled) {
            this.isMalwareDetectionEnabled = isMalwareDetectionEnabled;
        }

        public Boolean getIsPhishingDetectionEnabled() {
            return isPhishingDetectionEnabled;
        }

        public void setIsPhishingDetectionEnabled(Boolean isPhishingDetectionEnabled) {
            this.isPhishingDetectionEnabled = isPhishingDetectionEnabled;
        }
    }
}