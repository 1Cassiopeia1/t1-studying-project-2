package ru.t1.transaction.acceptation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionAcceptDto implements Serializable {
    private Long clientId;
    private Long accountId;
    private Long transactionId;
    private LocalDateTime timestamp;
    private String transactionAmount;
    private String accountBalance;
}