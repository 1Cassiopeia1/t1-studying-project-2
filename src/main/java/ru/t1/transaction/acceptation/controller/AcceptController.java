package ru.t1.transaction.acceptation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.transaction.acceptation.dto.TransactionAcceptDto;
import ru.t1.transaction.acceptation.service.AcceptService;

@RestController
@RequiredArgsConstructor
public class AcceptController {

    private final AcceptService acceptService;

    @PostMapping("/accept")
    public ResponseEntity<Void> saveEvent(@RequestBody TransactionAcceptDto acceptDto) {
        acceptService.saveEvent(acceptDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/block")
    public ResponseEntity<Boolean> isClientBlocked(@RequestParam Long clientId, @RequestParam Long accountId) {
        return ResponseEntity.ok().body(acceptService.isClientAccountsBlocked(clientId, accountId));
    }
}
