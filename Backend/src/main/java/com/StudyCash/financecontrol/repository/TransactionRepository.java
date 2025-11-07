package com.StudyCash.financecontrol.repository;

import com.StudyCash.financecontrol.model.Transaction;
import com.StudyCash.financecontrol.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndType(User user, Transaction.TransactionType type);
    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = ?1 ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactionsByUser(User user);
}