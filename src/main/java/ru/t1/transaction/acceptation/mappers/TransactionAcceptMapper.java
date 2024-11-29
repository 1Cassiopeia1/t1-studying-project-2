package ru.t1.transaction.acceptation.mappers;

import com.example.t1projectspringbootstarter.config.MapstructConfig;
import com.example.t1projectspringbootstarter.dto.TransactionAcceptDto;
import org.mapstruct.Mapper;
import ru.t1.transaction.acceptation.model.Event;

@Mapper(config = MapstructConfig.class)
public interface TransactionAcceptMapper {
    Event fromDtoToEntity(TransactionAcceptDto transactionAcceptDto);

}
