package com.megaloans.lms.config;

import com.megaloans.lms.model.*;
import com.megaloans.lms.repository.LateFeeDetailsRepository;
import com.megaloans.lms.repository.LoanDetailsRepository;
import com.megaloans.lms.repository.LoanPaymentRepository;
import com.megaloans.lms.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    @Autowired
    private LateFeeDetailsRepository lateFeeDetailsRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Initialize LoanDetails
            LoanDetails loanDetails = new LoanDetails();
            loanDetails.setUserId(1);
            loanDetails.setMutualFundId(101);
            loanDetails.setLoanAmount(50000.0);
            loanDetails.setInterestRate(5.0);
            loanDetails.setLoanTerm(36);
            loanDetails.setDisbursementDate(LocalDate.now());
            loanDetails.setMaturityDate(LocalDate.now().plusMonths(36));
            loanDetails.setOutstandingPrincipal(50000.0);
            loanDetails.setLoanStatus(LoanStatus.ACTIVE);

            // Save LoanDetails
            LoanDetails savedLoanDetails = loanDetailsRepository.save(loanDetails);

            // Initialize LoanPayments
            List<LoanPayment> payments = new ArrayList<>();

            LoanPayment loanPayment1 = new LoanPayment();
            loanPayment1.setLoan(savedLoanDetails);
            loanPayment1.setPaymentDate(LocalDate.now());
            loanPayment1.setPaymentAmount(1000.0);
            loanPayment1.setInterestPaid(0.0);
            loanPayment1.setPrincipalPaid(1000.0);
            loanPayment1.setLateFeeAmountPaid(0.0);
            loanPayment1.setMissedEmiNumber(0);
            payments.add(loanPayment1);

            LoanPayment loanPayment2 = new LoanPayment();
            loanPayment2.setLoan(savedLoanDetails);
            loanPayment2.setPaymentDate(LocalDate.now());
            loanPayment2.setPaymentAmount(0.0);
            loanPayment2.setInterestPaid(0.0);
            loanPayment2.setPrincipalPaid(0.0);
            loanPayment2.setLateFeeAmountPaid(0.0);
            loanPayment2.setMissedEmiNumber(0);

            // Initialize LateFeeDetails
            LateFeeDetails lateFeeDetails = new LateFeeDetails();
            lateFeeDetails.setFeeDate(LocalDate.now());
            lateFeeDetails.setLateFeeAmount(100.0);
            lateFeeDetails.setLoanPayment(loanPayment2);
            lateFeeDetails.setPaid(false);

            // Save entities with proper cascading
            loanPayment1.setLateFeeDetails(null); // or set if applicable
            loanPayment2.setLateFeeDetails(lateFeeDetails);
            payments.add(loanPayment2);

            savedLoanDetails.setPayments(payments);
            loanDetailsRepository.save(savedLoanDetails);

            // Save TransactionHistory
            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setLoanId(savedLoanDetails.getLoanId());
            transactionHistory.setTransactionDate(LocalDate.now());
            transactionHistory.setTransactionType(TransactionType.PAYMENT);
            transactionHistory.setTransactionSubType(TransactionSubType.EMI_PAYMENT);
            transactionHistory.setAmount(1000.0);
            transactionHistoryRepository.save(transactionHistory);
        };
    }
}
