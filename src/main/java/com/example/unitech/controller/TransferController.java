package com.example.unitech.controller;

import com.example.unitech.service.TransferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class TransferController {
    private final TransferService transferService;
private static final String TRANSFER="api/make-transfer";
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @PostMapping(TRANSFER)
    public ResponseEntity<String> makeTransfer(@RequestParam Long senderAccountId,
                                               @RequestParam Long recipientAccountId,
                                               @RequestParam BigDecimal amount) {
        try {
            transferService.makeTransfer(senderAccountId, recipientAccountId, amount);
            System.out.println("github test");
            return ResponseEntity.ok("Transfer successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
