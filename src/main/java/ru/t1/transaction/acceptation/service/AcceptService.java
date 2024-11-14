package ru.t1.transaction.acceptation.service;

import ru.t1.transaction.acceptation.dto.TransactionAcceptDto;

public interface AcceptService {

    void handleEvent(TransactionAcceptDto acceptDto);
}
