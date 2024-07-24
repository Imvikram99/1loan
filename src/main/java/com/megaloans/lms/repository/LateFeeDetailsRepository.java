package com.megaloans.lms.repository;

import com.megaloans.lms.model.LateFeeDetails;
import com.megaloans.lms.model.LoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LateFeeDetailsRepository extends JpaRepository<LateFeeDetails, Integer> {


    @Query("SELECT lfd FROM LateFeeDetails lfd WHERE lfd.loanPayment.loan.loanId = :loanId")
    List<LateFeeDetails> findLateFeesByLoanId(@Param("loanId") int loanId);

    @Query("SELECT lfd FROM LateFeeDetails lfd WHERE lfd.loanPayment.paymentId = :paymentId")
    List<LateFeeDetails> findLateFeesByPaymentId(@Param("paymentId") int paymentId);

}
