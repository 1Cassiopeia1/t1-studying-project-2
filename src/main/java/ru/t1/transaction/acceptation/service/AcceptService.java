package ru.t1.transaction.acceptation.service;

import ru.t1.transaction.acceptation.dto.TransactionAcceptDto;

public interface AcceptService {

    void saveEvent(TransactionAcceptDto acceptDto);

    void handleEvent(TransactionAcceptDto acceptDto);

    Boolean isClientAccountsBlocked(Long clientId, Long accountId);
}
