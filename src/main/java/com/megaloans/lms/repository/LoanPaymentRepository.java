package com.megaloans.lms.repository;

import com.megaloans.lms.model.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {
}
