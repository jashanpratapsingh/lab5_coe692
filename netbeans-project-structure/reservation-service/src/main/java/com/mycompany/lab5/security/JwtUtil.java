package com.mycompany.lab5.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class JwtUtil {
    /** Must match frontend TOKEN_COOKIE in assets/app.js */
    public static final String TOKEN_COOKIE_NAME = "lab5_token";

    private static final String JWT_SECRET_ENV = "JWT_SECRET";
    // Minimum 32 bytes when UTF-8 encoded (for HS256).
    private static final String FALLBACK_SECRET = "dev-lab5-jwt-secret-dev-lab5-jwt-secret";

    private JwtUtil() {}

    private static String resolveSecret() {
        String secret = System.getenv(JWT_SECRET_ENV);
        if (secret == null) return FALLBACK_SECRET;
        secret = secret.trim();
        if (secret.isEmpty()) return FALLBACK_SECRET;
        return secret.length() < 32 ? FALLBACK_SECRET : secret;
    }

    private static Key signingKey() {
        byte[] keyBytes = resolveSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) return false;
            Jwts.parser()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String getUsername(String token) {
        if (!validateToken(token)) return null;
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public static String readTokenFromCookieHeader(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) return null;
        String prefix = TOKEN_COOKIE_NAME + "=";
        for (String part : cookieHeader.split(";")) {
            String p = part.trim();
            if (p.startsWith(prefix)) {
                String raw = p.substring(prefix.length()).trim();
                try {
                    return URLDecoder.decode(raw, StandardCharsets.UTF_8.name());
                } catch (Exception e) {
                    return raw;
                }
            }
        }
        return null;
    }
}

