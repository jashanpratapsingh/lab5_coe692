package com.mycompany.lab5.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    /** Must match frontend TOKEN_COOKIE in assets/app.js */
    public static final String TOKEN_COOKIE_NAME = "lab5_token";
    public static final String SESSION_MARKER_HEADER = "X-LAB5-SESSION-MARKER";
    private static final String SESSION_MARKER_CLAIM = "sessionMarker";

    private static final String JWT_SECRET_ENV = "JWT_SECRET";
    // Minimum 32 bytes when UTF-8 encoded (for HS256).
    private static final String FALLBACK_SECRET = "dev-lab5-jwt-secret-dev-lab5-jwt-secret";
    private static final long DEFAULT_EXPIRATION_MS = 60L * 60L * 1000L; // 1 hour

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

    public static String createToken(String username, String sessionMarker) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username must be provided");
        }
        if (!isValidSessionMarker(sessionMarker)) {
            throw new IllegalArgumentException("sessionMarker must be provided");
        }

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + DEFAULT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(username)
                .claim(SESSION_MARKER_CLAIM, sessionMarker)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(signingKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createSessionMarker() {
        return UUID.randomUUID().toString().replace("-", "");
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

    public static boolean validateSessionMarker(String token, String markerHeader) {
        if (!validateToken(token) || !isValidSessionMarker(markerHeader)) return false;
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Object claimValue = claims.get(SESSION_MARKER_CLAIM);
        if (claimValue == null) return false;
        return markerHeader.equals(String.valueOf(claimValue));
    }

    private static boolean isValidSessionMarker(String marker) {
        if (marker == null) return false;
        String value = marker.trim();
        return value.matches("^[A-Za-z0-9]{16,64}$");
    }

    /**
     * Reads JWT from {@code Cookie} header value (e.g. {@code lab5_token=...}).
     * Value should be URL-encoded when set from JavaScript.
     */
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

