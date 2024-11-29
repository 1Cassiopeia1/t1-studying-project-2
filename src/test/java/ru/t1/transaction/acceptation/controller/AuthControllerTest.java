package ru.t1.transaction.acceptation.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.t1.transaction.acceptation.config.TestContainersConfig;
import ru.t1.transaction.acceptation.dto.JwtAuthenticationResponse;
import ru.t1.transaction.acceptation.dto.SignInRequest;
import ru.t1.transaction.acceptation.dto.SignUpRequest;
import ru.t1.transaction.acceptation.service.impl.security.AuthenticationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest implements TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void signUpTest() throws Exception {
        SignUpRequest signUpRequest = Instancio.of(SignUpRequest.class).create();
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("token");

        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    public void signInTest() throws Exception {
        SignInRequest signInRequest = Instancio.of(SignInRequest.class).create();
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("token");

        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }


}
