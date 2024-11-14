package ru.t1.transaction.acceptation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.transaction.acceptation.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByClientIdAndCreationTimestampGreaterThan(Long clientId, LocalDateTime creationTimestamp);
}
