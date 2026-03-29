package com.workouttrackerapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.workouttrackerapi.auth.dto.UserLoginRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationRequest;
import com.workouttrackerapi.auth.dto.UserRegistrationResponse;
import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.auth.repository.UserRepositories;
import com.workouttrackerapi.auth.service.AuthService;
import com.workouttrackerapi.auth.service.JwtsService;
import com.workouttrackerapi.common.exceptions.UserAllReadyExistedException;
import com.workouttrackerapi.common.exceptions.loginCredentialInvalidException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositories userRepositories;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtsService jwtsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_success() {

        UserRegistrationRequest request = new UserRegistrationRequest(
                "Sonu Kumar",
                "sonuku7294085931@gmail.com",
                "password");

        when(userRepositories.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");

        Users savedUser = new Users();
        savedUser.setId(1L);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setPassword("encoded");

        when(userRepositories.save(any(Users.class))).thenReturn(savedUser);

        UserRegistrationResponse response = authService.regiateruser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sonu Kumar", response.getName());
        assertEquals("sonuku7294085931@gmail.com", response.getEmail());

        verify(userRepositories).findByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepositories).save(any(Users.class));
    }

    @Test
    void registerUser_shouldThrowIfUserExists() {

        UserRegistrationRequest request = new UserRegistrationRequest(
                "Sonu Kumar",
                "test@gmail.com",
                "password");

        when(userRepositories.findByEmail(request.getEmail()))
                .thenReturn(new Users());

        assertThrows(UserAllReadyExistedException.class,
                () -> authService.regiateruser(request));

        verify(userRepositories).findByEmail(request.getEmail());
        verify(userRepositories, never()).save(any());
    }

    @Test
    void login_success() {

        UserLoginRequest request = new UserLoginRequest(
                "test@gmail.com",
                "password");

        Users user = new Users();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded");
        user.setRole("ROLE_USER");

        when(userRepositories.findByEmail(request.getEmail()))
                .thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtsService.generateToken(user.getEmail(), user.getRole()))
                .thenReturn("token123");

        Map<String, Object> response = authService.userLogin(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("token123", response.get("token"));

        verify(userRepositories).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtsService).generateToken(user.getEmail(), user.getRole());
    }

    @Test
    void login_shouldThrowIfUserNotFound() {

        UserLoginRequest request = new UserLoginRequest(
                "wrong@gmail.com",
                "password");

        when(userRepositories.findByEmail(request.getEmail()))
                .thenReturn(null);

        assertThrows(loginCredentialInvalidException.class,
                () -> authService.userLogin(request));

        verify(userRepositories).findByEmail(request.getEmail());
        verifyNoInteractions(passwordEncoder, jwtsService);
    }

    @Test
    void login_shouldThrowIfPasswordWrong() {

        UserLoginRequest request = new UserLoginRequest(
                "test@gmail.com",
                "wrong");

        Users user = new Users();
        user.setEmail("test@gmail.com");
        user.setPassword("encoded");
        user.setRole("ROLE_USER");

        when(userRepositories.findByEmail(request.getEmail()))
                .thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(loginCredentialInvalidException.class,
                () -> authService.userLogin(request));

        verify(userRepositories).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtsService, never()).generateToken(any(), any());
    }

    @Test
    void login_nullEmail() {

        UserLoginRequest request = new UserLoginRequest(null, null);

        when(userRepositories.findByEmail(null)).thenReturn(null);

        assertThrows(loginCredentialInvalidException.class,
                () -> authService.userLogin(request));
    }

    @Test
    void registerUser_verifyRoleAssignment() {

        UserRegistrationRequest request = new UserRegistrationRequest(
                "Test",
                "role@test.com",
                "pass");

        when(userRepositories.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        Users savedUser = new Users();
        savedUser.setId(1L);
        savedUser.setName("Test");
        savedUser.setEmail("role@test.com");

        when(userRepositories.save(any())).thenReturn(savedUser);

        authService.regiateruser(request);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(userRepositories).save(captor.capture());

        Users captured = captor.getValue();

        assertEquals("ROLE_USER", captured.getRole());
    }
}