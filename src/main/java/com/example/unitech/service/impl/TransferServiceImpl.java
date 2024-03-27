package com.example.unitech.service.impl;

import com.example.unitech.entity.Account;
import com.example.unitech.repository.AccountRepository;
import com.example.unitech.service.TransferService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {


    private final AccountRepository accountRepository;

    public TransferServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void makeTransfer(Long senderAccountId , Long recipientAccountId,BigDecimal amount) {
        Account fromAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Account toAccount = accountRepository.findById(recipientAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (!fromAccount.isActive() || !toAccount.isActive()) {
            throw new IllegalStateException("Cannot transfer to or from inactive accounts");
        }

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}


