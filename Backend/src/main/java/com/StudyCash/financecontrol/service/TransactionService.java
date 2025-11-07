package com.StudyCash.financecontrol.service;

import com.StudyCash.financecontrol.model.Transaction;
import com.StudyCash.financecontrol.model.User;
import com.StudyCash.financecontrol.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> findTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public List<Transaction> findTransactionsByUserAndType(User user, Transaction.TransactionType type) {
        return transactionRepository.findByUserAndType(user, type);
    }

    public List<Transaction> findTransactionsByUserAndDateRange(User user, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByUserAndTransactionDateBetween(user, start, end);
    }

    public List<Transaction> findRecentTransactionsByUser(User user) {
        return transactionRepository.findRecentTransactionsByUser(user);
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}