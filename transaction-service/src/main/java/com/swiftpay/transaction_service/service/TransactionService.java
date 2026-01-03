package com.swiftpay.transaction_service.service;

import com.swiftpay.transaction_service.dto.TransferRequest;
import com.swiftpay.transaction_service.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction create(Long SenderId, String idempotencyKey, TransferRequest transaction);
    List<Transaction> getAllTransactions();
    public Transaction getTransactionById(Long userId);
    public List<Transaction> getTransactionsByUser(Long userId);
}
