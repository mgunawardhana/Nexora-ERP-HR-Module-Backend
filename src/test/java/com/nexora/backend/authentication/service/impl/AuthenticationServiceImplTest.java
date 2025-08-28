package com.nexora.backend.authentication.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.authentication.repository.TokenRepository;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.request.AuthenticationRequest;
import com.nexora.backend.domain.request.RegistrationRequest;
import com.nexora.backend.domain.response.AuthenticationResponse;
import com.nexora.backend.util.ResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl Tests")
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmployeeDetailsRepository employeeDetailsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtServiceImpl jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ResponseUtil responseUtil;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequest registrationRequest;
    private AuthenticationRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registrationRequest = RegistrationRequest.builder()
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .build();
        authRequest = new AuthenticationRequest("test@example.com", "password");
        user = User.builder().id(1).email("test@example.com").password("encodedPassword").build();
    }

    @Test
    @DisplayName("Success: Register User")
    void register_Success() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        AuthenticationResponse response = authenticationService.register(registrationRequest);
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("Error: Register User with Existing Email")
    void register_EmailAlreadyExists() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Email already exists"));
        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Success: Authenticate User")
    void authenticate_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("newRefreshToken");
        AuthenticationResponse response = authenticationService.authenticate(authRequest);
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
    }

    @Test
    @DisplayName("Error: Authenticate User with Invalid Credentials")
    void authenticate_InvalidCredentials() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authenticationService.authenticate(authRequest))
                .isInstanceOf(RuntimeException.class);
    }
}