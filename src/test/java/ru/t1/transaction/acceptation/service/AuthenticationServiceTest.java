package ru.t1.transaction.acceptation.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.t1.transaction.acceptation.dto.JwtAuthenticationResponse;
import ru.t1.transaction.acceptation.dto.SignInRequest;
import ru.t1.transaction.acceptation.dto.SignUpRequest;
import ru.t1.transaction.acceptation.model.User;
import ru.t1.transaction.acceptation.service.impl.security.AuthenticationService;
import ru.t1.transaction.acceptation.service.impl.security.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for AuthenticationService")
public class AuthenticationServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthenticationService authService;

    private final String clientId = "testClientId";
    private final String clientSecret = "testClientSecret";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(authService, "clientId", clientId);
        ReflectionTestUtils.setField(authService, "clientSecret", clientSecret);
    }

    @Test
    public void testInit_userExists_doesNotCreateUser() {
        when(userService.existsByUsername(clientId)).thenReturn(true);

        authService.init();

        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testSignUp_createsUserAndReturnsToken() {
        SignUpRequest request = Instancio.of(SignUpRequest.class).create();
        when(passwordEncoder.encode(request.getPassword())).thenReturn(clientSecret);
        when(jwtService.generateToken(any())).thenReturn("token");

        JwtAuthenticationResponse response = authService.signUp(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(userService).create(any(User.class));
    }

    @Test
    public void testSignIn_authenticatesUserAndReturnsToken() {
        SignInRequest request = Instancio.of(SignInRequest.class).create();
        when(userService.getByUsername(request.getUsername())).thenReturn(new User());
        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        authService.signIn(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(UserDetails.class));
    }
}