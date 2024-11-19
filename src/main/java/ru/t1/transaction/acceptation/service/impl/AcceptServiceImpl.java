package ru.t1.transaction.acceptation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.t1.transaction.acceptation.constants.ErrorLogs;
import ru.t1.transaction.acceptation.dto.ResultDto;
import ru.t1.transaction.acceptation.dto.TransactionAcceptDto;
import ru.t1.transaction.acceptation.dto.enums.TransactionStatus;
import ru.t1.transaction.acceptation.kafka.KafkaProducer;
import ru.t1.transaction.acceptation.mappers.TransactionAcceptMapper;
import ru.t1.transaction.acceptation.model.Event;
import ru.t1.transaction.acceptation.repository.EventRepository;
import ru.t1.transaction.acceptation.service.AcceptService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcceptServiceImpl implements AcceptService {
    private final EventRepository eventRepository;
    private final TransactionAcceptMapper acceptMapper;
    private final KafkaProducer<ResultDto> resultDtoKafkaProducer;
    @Value("${t1.transaction-block.number}")
    private Long blockNumber;
    @Value("${t1.transaction-block.period}")
    private Duration blockPeriod;
    @Value("${t1.kafka.topic.t1_demo_transaction_result}")
    private String resultTopic;

    @Override
    public void saveEvent(TransactionAcceptDto acceptDto) {
        eventRepository.save(acceptMapper.fromDtoToEntity(acceptDto));
    }

    @Override
    public void handleEvent(TransactionAcceptDto acceptDto) {
        saveEvent(acceptDto);
        List<Event> events = eventRepository.findAllByClientIdAndCreationTimestampGreaterThan(
                acceptDto.getClientId(), LocalDateTime.now().minus(blockPeriod));

        ResultDto resultDto = ResultDto.builder()
                .accountId(acceptDto.getAccountId())
                .transactionId(acceptDto.getTransactionId())
                .build();

        if (events.size() >= blockNumber) {
            log.warn(ErrorLogs.TRANSACTION_BLOCKED, events.size(), acceptDto.getClientId());
            events.forEach(event -> {
                ResultDto resultModel = new ResultDto(TransactionStatus.BLOCKED,
                        event.getAccountId(), event.getTransactionId());
                resultDtoKafkaProducer.sendTo(resultTopic, resultModel, null);
                updateEventStatus(TransactionStatus.BLOCKED, event.getId());
            });
        } else if (new BigDecimal(acceptDto.getAccountBalance())
                .add(new BigDecimal(acceptDto.getTransactionAmount()))
                .compareTo(BigDecimal.ZERO) <= 0) {
            log.warn(ErrorLogs.TRANSACTION_REJECTED, acceptDto.getClientId());
            resultDto.setTransactionStatus(TransactionStatus.REJECTED);
            resultDtoKafkaProducer.sendTo(resultTopic, resultDto, null);
            updateEventStatus(TransactionStatus.REJECTED, acceptDto.getClientId());
        } else {
            resultDto.setTransactionStatus(TransactionStatus.ACCEPTED);
            resultDtoKafkaProducer.sendTo(resultTopic, resultDto, null);
            updateEventStatus(TransactionStatus.ACCEPTED, acceptDto.getClientId());
        }
    }

    @Override
    public Boolean isClientAccountsBlocked(Long clientId, Long accountId) {
        // если хотя бы одна транзакция по клиенту и счёту была заблокирована, то считаем клиента заблокированным
        return eventRepository.findAllByClientIdAndAccountId(clientId, accountId)
                .stream()
                .map(event -> event.getTransactionStatus() == TransactionStatus.BLOCKED)
                .reduce(false, (oldVal, newVal) -> oldVal || newVal);
    }

    private void updateEventStatus(TransactionStatus status, Long eventId) {
        eventRepository.updateTransactionStatusById(status, eventId);
    }
}
