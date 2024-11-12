package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.t1.java.demo.model.Accept;

import java.time.LocalDateTime;

public interface AcceptRepository extends JpaRepository<Accept, Long> {
    @Query("select a from Accept a where a.timestamp = :timestamp")
    Accept findByTimestamp(@Param("timestamp") LocalDateTime timestamp);
}
