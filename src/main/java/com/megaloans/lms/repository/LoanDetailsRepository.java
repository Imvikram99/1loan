package com.megaloans.lms.repository;

import com.megaloans.lms.model.LoanDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanDetailsRepository extends JpaRepository<LoanDetails, Integer> {

    // Find loan details with EMIs due today
    @Query("SELECT ld FROM LoanDetails ld JOIN ld.emiSchedule es WHERE es.paymentDay = DAY(CURRENT_DATE) AND es.paymentFrequency = 'MONTHLY'")
    List<LoanDetails> findByEMIScheduleDueToday();

    // Find loan details with EMIs due today, using pagination
    @Query("SELECT ld FROM LoanDetails ld JOIN ld.emiSchedule es WHERE es.paymentDay = DAY(CURRENT_DATE) AND es.paymentFrequency = 'MONTHLY'")
    List<LoanDetails> findByEMIScheduleDueToday(Pageable pageable);

    // Find loan details with EMIs due today, using pagination and offset
    @Query("SELECT ld FROM LoanDetails ld JOIN ld.emiSchedule es WHERE es.paymentDay = DAY(CURRENT_DATE) AND es.paymentFrequency = 'MONTHLY'")
    List<LoanDetails> findByEMIScheduleDueToday(Pageable pageable, @Param("offset") int offset);

    // Find loan details by loan ID
    @Query("SELECT ld FROM LoanDetails ld WHERE ld.loanId = :loanId")
    LoanDetails findByLoanId(@Param("loanId") int loanId);

    // Find loan details by user ID
    @Query("SELECT ld FROM LoanDetails ld WHERE ld.userId = :userId")
    List<LoanDetails> findByUserId(@Param("userId") int userId);
}
