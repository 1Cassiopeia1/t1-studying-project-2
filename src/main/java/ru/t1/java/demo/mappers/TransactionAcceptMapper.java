package ru.t1.java.demo.mappers;

import org.mapstruct.Mapper;
import ru.t1.java.demo.config.MapstructConfig;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.model.Accept;

@Mapper(config = MapstructConfig.class)
public interface TransactionAcceptMapper {
Accept fromDtoToEntity(TransactionAcceptDto transactionAcceptDto);
TransactionAcceptDto fromEntityToDto(Accept accept);
}
