package com.example.gatewaydemo.controller;

import com.example.gatewaydemo.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Value("${app.debug-log-tokens:false}") private boolean debugLogTokens;
    private final JwtService jwt;
    private final boolean secure;
    private final String sameSite;
    public AuthController(JwtService jwt, @Value("${app.cookie-secure}") boolean secure,
                          @Value("${app.cookie-same-site}") String sameSite) {
        this.jwt = jwt; this.secure = secure; this.sameSite = sameSite;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        if (!"demo".equals(request.username()) || !"demo123".equals(request.password())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        String accessToken = jwt.issue("demo");
        if (debugLogTokens) log.warn("[DEMO-AUTH] LOGIN user=demo JWT={} (sent via HttpOnly ACCESS_TOKEN cookie)", accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, authCookie(accessToken, jwt.ttlSeconds()).toString());
        return ResponseEntity.ok(Map.of("username", "demo"));
    }

    @GetMapping("/auth/me") public Map<String, String> me(Authentication auth) {
        return Map.of("username", auth.getName());
    }

    @PostMapping("/auth/logout") public Map<String, String> logout(HttpServletResponse response) {
        if (debugLogTokens) log.warn("[DEMO-AUTH] LOGOUT clearing ACCESS_TOKEN cookie");
        response.addHeader(HttpHeaders.SET_COOKIE, authCookie("", 0).toString());
        return Map.of("message", "Logged out");
    }

    private ResponseCookie authCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from("ACCESS_TOKEN", value)
            .httpOnly(true).secure(secure).sameSite(sameSite)
            .path("/api").maxAge(maxAgeSeconds).build();
    }
}
