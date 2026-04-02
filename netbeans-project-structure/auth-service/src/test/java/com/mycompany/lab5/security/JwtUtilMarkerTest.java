package com.mycompany.lab5.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JwtUtilMarkerTest {
    @Test
    void markerAndTokenMustMatch() {
        String marker = JwtUtil.createSessionMarker();
        String token = JwtUtil.createToken("student1", marker);

        Assertions.assertTrue(JwtUtil.validateToken(token));
        Assertions.assertTrue(JwtUtil.validateSessionMarker(token, marker));
        Assertions.assertFalse(JwtUtil.validateSessionMarker(token, marker + "X"));
    }

    @Test
    void invalidMarkerShapeIsRejected() {
        String marker = JwtUtil.createSessionMarker();
        String token = JwtUtil.createToken("student1", marker);
        Assertions.assertFalse(JwtUtil.validateSessionMarker(token, "bad-marker"));
        Assertions.assertFalse(JwtUtil.validateSessionMarker(token, ""));
        Assertions.assertFalse(JwtUtil.validateSessionMarker(token, null));
    }
}
