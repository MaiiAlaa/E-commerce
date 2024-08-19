package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
}
