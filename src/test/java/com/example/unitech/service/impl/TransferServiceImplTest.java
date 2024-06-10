package com.example.unitech.service.impl;
import com.example.unitech.entity.Account;
import com.example.unitech.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMakeTransfer_SuccessfulTransfer() {
        Long senderAccountId = 1L;
        Long recipientAccountId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAccount = new Account();
        fromAccount.setId(senderAccountId);
        fromAccount.setBalance(new BigDecimal("500.00"));
        fromAccount.setActive(true);

        Account toAccount = new Account();
        toAccount.setId(recipientAccountId);
        toAccount.setBalance(new BigDecimal("200.00"));
        toAccount.setActive(true);

        when(accountRepository.findById(senderAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(recipientAccountId)).thenReturn(Optional.of(toAccount));

        assertDoesNotThrow(() -> transferService.makeTransfer(senderAccountId, recipientAccountId, amount));

        assertEquals(new BigDecimal("400.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), toAccount.getBalance());

        verify(accountRepository, times(2)).findById(anyLong());

        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void testMakeTransfer_InsufficientBalance() {
        Long senderAccountId = 1L;
        Long recipientAccountId = 2L;
        BigDecimal amount = new BigDecimal("1000.00");

        Account fromAccount = new Account();
        fromAccount.setId(senderAccountId);
        fromAccount.setBalance(new BigDecimal("500.00"));
        fromAccount.setActive(true);

        Account toAccount = new Account();
        toAccount.setId(recipientAccountId);
        toAccount.setBalance(new BigDecimal("200.00"));
        toAccount.setActive(true);

        when(accountRepository.findById(senderAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(recipientAccountId)).thenReturn(Optional.of(toAccount));

        assertThrows(IllegalArgumentException.class, () -> transferService.makeTransfer(senderAccountId, recipientAccountId, amount));

        verify(accountRepository, times(2)).findById(anyLong());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testMakeTransfer_ToSameAccount() {
        Long senderAccountId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAccount = new Account();
        fromAccount.setId(senderAccountId);
        fromAccount.setActive(true);

        when(accountRepository.findById(senderAccountId)).thenReturn(Optional.of(fromAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.makeTransfer(senderAccountId, senderAccountId, amount));
        assertEquals("Cannot transfer to the same account", exception.getMessage());

        verify(accountRepository, times(2)).findById(senderAccountId);

        verify(accountRepository, never()).save(any(Account.class));
    }


    @Test
    void testMakeTransfer_ToDeactiveAccount() {
        Long senderAccountId = 1L;
        Long recipientAccountId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAccount = new Account();
        fromAccount.setId(senderAccountId);
        fromAccount.setActive(true);

        Account toAccount = new Account();
        toAccount.setId(recipientAccountId);
        toAccount.setActive(false);

        when(accountRepository.findById(senderAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(recipientAccountId)).thenReturn(Optional.of(toAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> transferService.makeTransfer(senderAccountId, recipientAccountId, amount));
        assertEquals("Cannot transfer to or from inactive accounts", exception.getMessage());

        verify(accountRepository, times(2)).findById(anyLong());

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testMakeTransfer_ToNonExistingAccount() {

        Long senderAccountId = 1L;
        Long recipientAccountId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAccount = new Account();
        fromAccount.setId(senderAccountId);
        fromAccount.setActive(true);

        when(accountRepository.findById(senderAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(recipientAccountId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> transferService.makeTransfer(senderAccountId, recipientAccountId, amount));
        assertEquals("Account not found", exception.getMessage());

        verify(accountRepository, times(2)).findById(anyLong());

        verify(accountRepository, never()).save(any(Account.class));
    }

}
