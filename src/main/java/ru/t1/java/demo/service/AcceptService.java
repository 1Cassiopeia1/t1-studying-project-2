package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.ResultDto;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.model.Accept;

import java.time.LocalDateTime;

public interface AcceptService {

    Accept getAccept(LocalDateTime timestamp);

    void saveAccept(TransactionAcceptDto acceptDto);

    ResultDto handleAccept(TransactionAcceptDto acceptDto);
}
