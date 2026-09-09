package com.blog.demo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "bVYp7uF83m6Zs8jF4kLd9xP0rTn2qWe5yHcXzBvN8mRq3sGfD1aJkLpOeRtYwUi";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", TEST_SECRET);
    }

    @Test
    void testGenerateAndExtractUsername() {
        UserDetails userDetails = new User("ouda", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String username = jwtUtil.extractUsername(token);
        assertEquals("ouda", username);
    }

    @Test
    void testIsTokenValid() {
        UserDetails userDetails = new User("ahmed", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.isTokenValid(token, userDetails));

        UserDetails otherUser = new User("differentUser", "password", Collections.emptyList());
        assertFalse(jwtUtil.isTokenValid(token, otherUser));
    }

    @Test
    void testIsNotTokenExpired() {
        UserDetails userDetails = new User("tester", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.isNotTokenExpired(token));
    }
}
