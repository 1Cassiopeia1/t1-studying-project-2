package ru.t1.transaction.acceptation.mappers;

import org.mapstruct.Mapper;
import ru.t1.transaction.acceptation.dto.TransactionAcceptDto;
import ru.t1.transaction.acceptation.config.MapstructConfig;
import ru.t1.transaction.acceptation.model.Event;

@Mapper(config = MapstructConfig.class)
public interface TransactionAcceptMapper {
    Event fromDtoToEntity(TransactionAcceptDto transactionAcceptDto);

}
