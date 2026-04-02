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
import com.workouttrackerapi.auth.enums.Role;
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

        // Use a dummy implementation instead of mocking JwtsService
        private JwtsService jwtsService = new JwtsService() {
                @Override
                public String generateToken(String email, Role role) {
                        return email + "-dummy-token";
                }
        };

        @InjectMocks
        private AuthService authService;

        @Test
        void registerUser_success() {
                UserRegistrationRequest request = new UserRegistrationRequest(
                                "Sonu Kumar",
                                "sonu@gmail.com",
                                "password123");

                when(userRepositories.findByEmail(request.getEmail())).thenReturn(null);
                when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

                Users savedUser = new Users();
                savedUser.setId(1L);
                savedUser.setName(request.getName());
                savedUser.setEmail(request.getEmail());
                savedUser.setPassword("encodedPassword");
                savedUser.setRole(Role.USER);

                when(userRepositories.save(any(Users.class))).thenReturn(savedUser);

                UserRegistrationResponse response = authService.regiateruser(request);

                assertNotNull(response);
                assertEquals(1L, response.getId());
                assertEquals("Sonu Kumar", response.getName());
                assertEquals("sonu@gmail.com", response.getEmail());

                verify(userRepositories).findByEmail(request.getEmail());
                verify(passwordEncoder).encode(request.getPassword());
                verify(userRepositories).save(any(Users.class));
        }

        @Test
        void registerUser_shouldThrowIfUserAlreadyExists() {
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
        void registerUser_shouldAssignDefaultRole_USER() {
                UserRegistrationRequest request = new UserRegistrationRequest(
                                "Test User",
                                "role@test.com",
                                "password");

                when(userRepositories.findByEmail(request.getEmail())).thenReturn(null);
                when(passwordEncoder.encode(any())).thenReturn("encoded");

                Users savedUser = new Users();
                savedUser.setId(1L);
                savedUser.setName("Test User");
                savedUser.setEmail("role@test.com");

                when(userRepositories.save(any())).thenReturn(savedUser);

                authService.regiateruser(request);

                ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
                verify(userRepositories).save(captor.capture());

                Users capturedUser = captor.getValue();

                assertEquals(Role.USER, capturedUser.getRole());
                assertEquals("encoded", capturedUser.getPassword());
        }

        @Test
        void login_success_user() {
                UserLoginRequest request = new UserLoginRequest(
                                "user@gmail.com",
                                "password");

                Users user = new Users();
                user.setEmail("user@gmail.com");
                user.setPassword("encodedPassword");
                user.setRole(Role.USER);

                when(userRepositories.findByEmail(request.getEmail())).thenReturn(user);
                when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

                Map<String, Object> response = authService.userLogin(request);

                assertNotNull(response);
                assertTrue(response.containsKey("token"));
                assertEquals("user@gmail.com-dummy-token", response.get("token"));

                verify(userRepositories).findByEmail(request.getEmail());
                verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        }

        @Test
        void login_success_admin() {
                UserLoginRequest request = new UserLoginRequest(
                                "admin@gmail.com",
                                "password");

                Users admin = new Users();
                admin.setEmail("admin@gmail.com");
                admin.setPassword("encodedPassword");
                admin.setRole(Role.ADMIN);

                when(userRepositories.findByEmail(request.getEmail())).thenReturn(admin);
                when(passwordEncoder.matches(request.getPassword(), admin.getPassword())).thenReturn(true);

                Map<String, Object> response = authService.userLogin(request);

                assertNotNull(response);
                assertEquals("admin@gmail.com-dummy-token", response.get("token"));
        }

        @Test
        void login_shouldThrowIfUserNotFound() {
                UserLoginRequest request = new UserLoginRequest(
                                "wrong@gmail.com",
                                "password");

                when(userRepositories.findByEmail(request.getEmail())).thenReturn(null);

                assertThrows(loginCredentialInvalidException.class,
                                () -> authService.userLogin(request));

                verify(userRepositories).findByEmail(request.getEmail());
                verifyNoInteractions(passwordEncoder);
        }

        @Test
        void login_shouldThrowIfPasswordIncorrect() {
                UserLoginRequest request = new UserLoginRequest(
                                "user@gmail.com",
                                "wrongPassword");

                Users user = new Users();
                user.setEmail("user@gmail.com");
                user.setPassword("encodedPassword");
                user.setRole(Role.USER);

                when(userRepositories.findByEmail(request.getEmail())).thenReturn(user);
                when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

                assertThrows(loginCredentialInvalidException.class,
                                () -> authService.userLogin(request));

                verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        }

        @Test
        void login_shouldThrowIfEmailIsNull() {
                UserLoginRequest request = new UserLoginRequest(null, "password");

                when(userRepositories.findByEmail(null)).thenReturn(null);

                assertThrows(loginCredentialInvalidException.class,
                                () -> authService.userLogin(request));
        }
}