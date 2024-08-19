package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> getAllTransactions();
}
