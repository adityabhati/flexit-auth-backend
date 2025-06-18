package com.flexit.user_management.components;

import com.flexit.user_management.util.constants.IniConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(Configuration iniConfiguration) {
        this.secretKey = Keys.hmacShaKeyFor(iniConfiguration.getString(IniConstant.USER_PASS_ENCRYPTION_KEY).getBytes());
    }

    public String generateToken(String username, String role, Long validity) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, role, validity);
    }

    private String createToken(Map<String, Object> claims, String email, String role, Long validity) {
        claims.put("role", role);
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(Instant.now().toEpochMilli()))
                .expiration(new Date(Instant.now().toEpochMilli() + validity))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isRefreshTokenExpired(token));
    }

    private boolean isRefreshTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshTokenExpired(Long expiryDate) {
        return expiryDate <= Instant.now().toEpochMilli();
    }
}