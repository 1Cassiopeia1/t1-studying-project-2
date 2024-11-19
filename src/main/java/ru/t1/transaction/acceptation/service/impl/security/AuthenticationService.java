package ru.t1.transaction.acceptation.service.impl.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.t1.transaction.acceptation.dto.JwtAuthenticationResponse;
import ru.t1.transaction.acceptation.dto.SignInRequest;
import ru.t1.transaction.acceptation.dto.SignUpRequest;
import ru.t1.transaction.acceptation.dto.enums.Role;
import ru.t1.transaction.acceptation.model.User;
import ru.t1.transaction.acceptation.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Value("${spring.security.oauth2.client.registration.external.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.external.client-secret}")
    private String clientSecret;

    @PostConstruct
    public void init() {
        if (!userService.existsByUsername(clientId)) {
            var user = User.builder()
                    .username(clientId)
                    .password(passwordEncoder.encode(clientSecret))
                    .role(Role.TECHNICAL_USER)
                    .build();
            userService.create(user);
        }
    }

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService.getByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}