package ru.t1.transaction.acceptation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.transaction.acceptation.config.TestContainersConfig;
import ru.t1.transaction.acceptation.dto.TransactionAcceptDto;
import ru.t1.transaction.acceptation.service.AcceptService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptControllerTest implements TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AcceptService acceptService;

    @Test
    public void saveEventTest() throws Exception {
        TransactionAcceptDto acceptDto = Instancio.of(TransactionAcceptDto.class)
                        .create();
        mockMvc.perform(post("/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(acceptDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void isClientBlockedTest() throws Exception {
        Long clientId = 1L;
        Long accountId = 1L;
        when(acceptService.isClientAccountsBlocked(clientId, accountId)).thenReturn(true);

        mockMvc.perform(get("/block")
                        .param("clientId", String.valueOf(clientId))
                        .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }
}
