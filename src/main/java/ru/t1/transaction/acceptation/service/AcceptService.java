package ru.t1.transaction.acceptation.service;

import com.example.t1projectspringbootstarter.dto.TransactionAcceptDto;

public interface AcceptService {

    void saveEvent(TransactionAcceptDto acceptDto);

    void handleEvent(TransactionAcceptDto acceptDto);

    Boolean isClientAccountsBlocked(Long clientId, Long accountId);
}
