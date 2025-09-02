package com.phantom.security.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test") // /api는 컨텍스트 패스에서 자동 처리
public class TestController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "pong", "status", "OK");
    }

    @PostMapping("/auth-test")
    public Map<String, String> authTest(@RequestBody(required = false) Map<String, Object> body) {
        return Map.of(
                "message", "Auth endpoint test successful",
                "received", body != null ? body.toString() : "no body"
        );
    }
}