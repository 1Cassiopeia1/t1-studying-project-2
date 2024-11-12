package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ResultDto;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.mappers.TransactionAcceptMapper;
import ru.t1.java.demo.model.Accept;
import ru.t1.java.demo.repository.AcceptRepository;
import ru.t1.java.demo.service.AcceptService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcceptServiceImpl implements AcceptService {
    private final AcceptRepository acceptRepository;
private final TransactionAcceptMapper acceptMapper;

    @Override
    public Accept getAccept(LocalDateTime timestamp) {
       return acceptRepository.findByTimestamp(timestamp);
    }

    @Override
    public void saveAccept(TransactionAcceptDto acceptDto) {
        acceptRepository.save(acceptMapper.fromDtoToEntity(acceptDto));
    }

    @Override
    public ResultDto handleAccept(TransactionAcceptDto acceptDto){
        return;
    }

}
