package com.example.gatewaydemo.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {
    private static final Base64.Encoder ENC = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DEC = Base64.getUrlDecoder();
    private final byte[] secret;
    private final long ttlSeconds;
    private final ObjectMapper mapper;

    public JwtService(@Value("${app.jwt-secret}") String secret,
                      @Value("${app.token-ttl-seconds}") long ttlSeconds, ObjectMapper mapper) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) throw new IllegalArgumentException("JWT_SECRET must be at least 32 UTF-8 bytes");
        this.ttlSeconds = ttlSeconds;
        this.mapper = mapper;
    }

    public String issue(String username) {
        try {
            long now = Instant.now().getEpochSecond();
            String header = encode(mapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = encode(mapper.writeValueAsBytes(Map.of("sub", username, "iat", now, "exp", now + ttlSeconds)));
            String input = header + "." + payload;
            return input + "." + encode(sign(input));
        } catch (Exception e) { throw new IllegalStateException("Cannot create JWT", e); }
    }

    public String verifyAndGetSubject(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) return null;
            byte[] actual = DEC.decode(parts[2]);
            if (!MessageDigest.isEqual(sign(parts[0] + "." + parts[1]), actual)) return null;
            JsonNode header = mapper.readTree(DEC.decode(parts[0]));
            if (!"HS256".equals(header.path("alg").asText())) return null;
            JsonNode payload = mapper.readTree(DEC.decode(parts[1]));
            if (!payload.has("exp") || !payload.get("exp").canConvertToLong()) return null;
            if (Instant.now().getEpochSecond() >= payload.get("exp").asLong()) return null;
            String subject = payload.path("sub").asText("");
            return subject.isBlank() ? null : subject;
        } catch (Exception e) { return null; }
    }

    public long ttlSeconds() { return ttlSeconds; }
    private static String encode(byte[] bytes) { return ENC.encodeToString(bytes); }
    private byte[] sign(String input) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
    }
}
