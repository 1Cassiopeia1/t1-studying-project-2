package ru.t1.java.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accept")
public class Accept {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accept_generator")
    @SequenceGenerator(name = "accept_generator", sequenceName = "accept_seq")
    private Long id;
    private Long clientId;
    private Long accountId;
    private Long transactionId;
    private LocalDateTime timestamp;
    private String transactionAmount;
    private String accountBalance;
}
