package com.megaloans.lms.repository;

import com.megaloans.lms.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
    // Additional query methods can be defined here if needed
}
