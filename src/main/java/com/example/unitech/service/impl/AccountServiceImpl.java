package com.example.unitech.service.impl;

import com.example.unitech.entity.Account;
import com.example.unitech.entity.User;
import com.example.unitech.repository.AccountRepository;
import com.example.unitech.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }




    @Override
    public List<BigDecimal> getActiveAccountsBalance(User loggedInUser) {
        List<Account> activeAccounts = accountRepository.findByUserAndIsActiveTrue(loggedInUser);
        return activeAccounts.stream()
                .map(Account::getBalance)
                .collect(Collectors.toList());    }



}
