package ru.t1.transaction.acceptation.dto;

import com.example.t1projectspringbootstarter.dto.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultDto implements Serializable {
    private TransactionStatus transactionStatus;
    private Long accountId;
    private Long transactionId;
}