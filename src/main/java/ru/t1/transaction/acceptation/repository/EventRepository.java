package ru.t1.transaction.acceptation.repository;

import com.example.t1projectspringbootstarter.dto.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.transaction.acceptation.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByClientIdAndCreationTimestampGreaterThan(Long clientId, LocalDateTime creationTimestamp);

    @Transactional
    @Modifying
    @Query("update Event e set e.transactionStatus = :transactionStatus where e.id = :id")
    void updateTransactionStatusById(TransactionStatus transactionStatus, Long id);

    List<Event> findAllByClientIdAndAccountId(Long clientId, Long accountId);
}
