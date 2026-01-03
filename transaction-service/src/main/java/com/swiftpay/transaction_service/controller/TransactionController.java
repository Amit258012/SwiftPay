package com.swiftpay.transaction_service.controller;

import com.swiftpay.transaction_service.dto.TransferRequest;
import com.swiftpay.transaction_service.entity.Transaction;
import com.swiftpay.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Transaction> create(@RequestHeader("X-User-Id") Long userId, @RequestBody TransferRequest request) {
        transactionService.create(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public List<Transaction> getAll(){
        return transactionService.getAllTransactions();
    }

}
