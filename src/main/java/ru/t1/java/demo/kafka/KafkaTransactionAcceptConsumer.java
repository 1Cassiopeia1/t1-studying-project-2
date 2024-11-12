package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.service.AcceptService;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionAcceptConsumer {

    private final AcceptService acceptService;

    //TODO менять consumerGroupId
    @RetryableTopic(
            attempts = "1",
            kafkaTemplate = "kafkaTemplate",
            dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.t1_demo_transaction_result}",
            containerFactory = "accountKafkaListenerContainerFactory")
    public void receiveAccept(@Payload TransactionAcceptDto transactionAcceptDto,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                              Acknowledgment ack) {
        handleAccept(transactionAcceptDto, topic, key, ack);
    }

    @DltHandler
    public void receiveAcceptDlt(@Payload TransactionAcceptDto transactionAcceptDto,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                 Acknowledgment ack) {
        handleAccept(transactionAcceptDto, topic, key, ack);
    }

    @DltHandler
    public void handleAccept(TransactionAcceptDto transactionAcceptDto, String topic,
                             String key, Acknowledgment ack) {
        acceptService.handleAccept(transactionAcceptDto);
    }
}
