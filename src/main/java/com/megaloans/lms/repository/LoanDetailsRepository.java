package com.megaloans.lms.repository;

import com.megaloans.lms.model.LoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanDetailsRepository extends JpaRepository<LoanDetails, Integer> {
}
