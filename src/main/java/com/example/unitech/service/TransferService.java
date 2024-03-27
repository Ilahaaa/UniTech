package com.example.unitech.service;

import java.math.BigDecimal;

public interface TransferService {
    void makeTransfer(Long senderAccountId, Long recipientAccountId, BigDecimal amount);
}
