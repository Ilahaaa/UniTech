package com.example.unitech.service.impl;
import com.example.unitech.entity.Account;
import com.example.unitech.entity.User;
import com.example.unitech.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActiveAccountsBalance() {
        User loggedInUser = new User();

        Account account1 = new Account();
        account1.setBalance(new BigDecimal("100.00"));
        account1.setActive(true);
        account1.setUser(loggedInUser);
        Account account2 = new Account();
        account2.setBalance(new BigDecimal("200.00"));
        account2.setActive(true);
        account2.setUser(loggedInUser);
        List<Account> activeAccounts = new ArrayList<>();
        activeAccounts.add(account1);
        activeAccounts.add(account2);

        when(accountRepository.findByUserAndIsActiveTrue(loggedInUser)).thenReturn(activeAccounts);

        List<BigDecimal> balances = accountService.getActiveAccountsBalance(loggedInUser);

        assertEquals(2, balances.size());
        assertEquals(new BigDecimal("100.00"), balances.get(0));
        assertEquals(new BigDecimal("200.00"), balances.get(1));

        verify(accountRepository, times(1)).findByUserAndIsActiveTrue(loggedInUser);
    }
}
