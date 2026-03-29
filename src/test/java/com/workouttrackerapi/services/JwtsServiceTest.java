package com.workouttrackerapi.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.workouttrackerapi.auth.service.JwtsService;

@ExtendWith(MockitoExtension.class)
class JwtsServiceTest {

    @InjectMocks
    private JwtsService jwtsService;

    @Test
    void generateToken_shouldWork() {
        String token = jwtsService.generateToken(
                "sonuku7294085931@gmail.com",
                "ROLE_USER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}