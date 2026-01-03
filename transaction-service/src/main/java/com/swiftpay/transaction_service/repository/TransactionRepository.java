package com.swiftpay.transaction_service.repository;

import com.swiftpay.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderIdOrReceiverId(Long userId, Long userId1);

    Optional<Transaction> findBySenderIdAndIdempotencyKey(Long userId, String idempotencyKey);
}
