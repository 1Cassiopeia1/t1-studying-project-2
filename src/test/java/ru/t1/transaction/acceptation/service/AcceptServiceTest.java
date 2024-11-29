package ru.t1.transaction.acceptation.service;

import com.example.t1projectspringbootstarter.config.KafkaProducer;
import com.example.t1projectspringbootstarter.dto.TransactionAcceptDto;
import com.example.t1projectspringbootstarter.dto.enums.TransactionStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.t1.transaction.acceptation.constants.ErrorLogs;
import ru.t1.transaction.acceptation.dto.ResultDto;
import ru.t1.transaction.acceptation.mappers.TransactionAcceptMapper;
import ru.t1.transaction.acceptation.model.Event;
import ru.t1.transaction.acceptation.repository.EventRepository;
import ru.t1.transaction.acceptation.service.impl.AcceptServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
@DisplayName("Test for AcceptService")
public class AcceptServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private KafkaProducer<ResultDto> kafkaProducer;
    @Mock
    private TransactionAcceptMapper transactionAcceptMapper;
    @Captor
    private ArgumentCaptor<ResultDto> resultDtoCaptor;
    @InjectMocks
    private AcceptServiceImpl acceptService;

    private static final String resultTopic = "resultTopic";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(acceptService, "blockPeriod", Duration.ofSeconds(60));
        ReflectionTestUtils.setField(acceptService, "blockNumber", 3L);
        ReflectionTestUtils.setField(acceptService, "resultTopic", "resultTopic");
    }

    @Test
    void handleEventBlockedResultTest(CapturedOutput capturedOutput) {
        // given
        TransactionAcceptDto acceptDto = new TransactionAcceptDto()
                .setClientId(2L)
                .setAccountId(3L)
                .setTransactionId(1L)
                .setAccountBalance("100")
                .setTransactionAmount("50");

        Event event1 = new Event().setId(1L).setClientId(2L).setTransactionId(1L);
        Event event2 = new Event().setId(2L).setClientId(2L).setTransactionId(2L);
        Event event3 = new Event().setId(3L).setClientId(2L).setTransactionId(3L);

        when(eventRepository.findAllByClientIdAndCreationTimestampGreaterThan(
                eq(acceptDto.getClientId()), any(LocalDateTime.class)))
                .thenReturn(List.of(event1, event2, event3));

        // when
        acceptService.handleEvent(acceptDto);

        // then
        verify(kafkaProducer, times(3)).sendTo(eq(resultTopic), resultDtoCaptor.capture(), isNull());
        verify(eventRepository, times(3)).updateTransactionStatusById(eq(TransactionStatus.BLOCKED), anyLong());
        assertEquals(TransactionStatus.BLOCKED, resultDtoCaptor.getValue().getTransactionStatus());
        assertTrue(capturedOutput.getOut().contains(
                ErrorLogs.TRANSACTION_BLOCKED.replaceFirst("\\{}", "3").replaceFirst("\\{}", "2")));

    }

    @Test
    public void handleEventRejectedResultTest(CapturedOutput capturedOutput) {
        // given
        TransactionAcceptDto acceptDto = new TransactionAcceptDto()
                .setClientId(2L)
                .setAccountId(3L)
                .setTransactionId(1L)
                .setAccountBalance("-100")
                .setTransactionAmount("-50");

        when(eventRepository.findAllByClientIdAndCreationTimestampGreaterThan(
                eq(acceptDto.getClientId()), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // when
        acceptService.handleEvent(acceptDto);

        // then
        ResultDto expectedResultDto = ResultDto.builder()
                .accountId(acceptDto.getAccountId())
                .transactionId(acceptDto.getTransactionId())
                .transactionStatus(TransactionStatus.REJECTED)
                .build();

        verify(kafkaProducer).sendTo(eq(resultTopic), eq(expectedResultDto), isNull());
        verify(eventRepository).updateTransactionStatusById(eq(TransactionStatus.REJECTED), anyLong());
        assertTrue(capturedOutput.getOut().contains(ErrorLogs.TRANSACTION_REJECTED.replaceAll("\\{}", "2")));
    }

    @Test
    public void handleEventAcceptedResultTest() {
        // given
        TransactionAcceptDto acceptDto = new TransactionAcceptDto()
                .setClientId(2L)
                .setAccountId(3L)
                .setTransactionId(1L)
                .setAccountBalance("100")
                .setTransactionAmount("50");

        when(eventRepository.findAllByClientIdAndCreationTimestampGreaterThan(
                eq(acceptDto.getClientId()), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // when
        acceptService.handleEvent(acceptDto);

        // then
        ResultDto expectedResultDto = ResultDto.builder()
                .accountId(acceptDto.getAccountId())
                .transactionId(acceptDto.getTransactionId())
                .transactionStatus(TransactionStatus.ACCEPTED)
                .build();

        verify(kafkaProducer).sendTo(eq(resultTopic), eq(expectedResultDto), isNull());
        verify(eventRepository).updateTransactionStatusById(eq(TransactionStatus.ACCEPTED), anyLong());
    }

    @Test
    public void handleEventExceptionTest() {
        // given
        when(eventRepository.findAllByClientIdAndCreationTimestampGreaterThan(any(), any()))
                .thenThrow(new EntityNotFoundException("Entity not found"));

        // when
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> acceptService.handleEvent(new TransactionAcceptDto()));

        // then
        assertNotNull(actual);
        assertEquals("Entity not found", actual.getMessage());
    }
}
