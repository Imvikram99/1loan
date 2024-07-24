package com.megaloans.lms.service;

import com.megaloans.lms.dto.request.LoanCreationRequest;
import com.megaloans.lms.dto.request.RepaymentRequest;
import com.megaloans.lms.model.*;
import com.megaloans.lms.repository.LateFeeDetailsRepository;
import com.megaloans.lms.repository.LoanDetailsRepository;
import com.megaloans.lms.repository.LoanPaymentRepository;
import com.megaloans.lms.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    @Autowired
    private LateFeeDetailsRepository lateFeeDetailsRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)//assuming money will be transferred to user bank account
    public LoanDetails createLoan(LoanCreationRequest request) {
        LoanDetails loanDetails = new LoanDetails();
        loanDetails.setUserId(request.getUserId());
        loanDetails.setMutualFundId(request.getMutualFundId());
        loanDetails.setLoanAmount(request.getLoanAmount());
        loanDetails.setInterestRate(request.getInterestRate());
        loanDetails.setLoanTerm(request.getLoanTerm());
        loanDetails.setDisbursementDate(request.getDisbursementDate());
        loanDetails.setMaturityDate(request.getDisbursementDate().plusMonths(request.getLoanTerm()));
        loanDetails.setOutstandingPrincipal(request.getLoanAmount());
        loanDetails.setLoanStatus(LoanStatus.ACTIVE);
        LoanDetails savedLoanDetails = loanDetailsRepository.save(loanDetails);
        // Create a TransactionHistory entry, ideally we can have a loan disbursed table which will have all the account
        // details like from which account to which account this transfer happened but for brevity im here
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setLoanId(savedLoanDetails.getLoanId());
        transactionHistory.setTransactionDate(LocalDate.now());
        transactionHistory.setTransactionType(TransactionType.DISBURSEMENT);
        transactionHistory.setAmount(savedLoanDetails.getLoanAmount());
        // Save the TransactionHistory
        transactionHistoryRepository.save(transactionHistory);

        return savedLoanDetails;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LoanPayment makeRepayment(RepaymentRequest request) {
        Optional<LoanDetails> loanDetailsOptional = loanDetailsRepository.findById(request.getLoanId());
        if (!loanDetailsOptional.isPresent()) {
            throw new IllegalArgumentException("Loan not found");
        }


        LoanDetails loanDetails = loanDetailsOptional.get();
        if(loanDetails.getLoanStatus()!=LoanStatus.ACTIVE){
            throw new RuntimeException("loan is not active") ;
        }
        double paymentAmount = request.getPaymentAmount();
        double remainingAmount = paymentAmount;
        double lateFeePaid = 0.0;
        double lateFeeInterestPaid = 0.0;
        double accruedInterestPaid = 0.0;
        double principalPaid = 0.0;

        // 1. Knock off late fees first
      List<LateFeeDetails> lateFeeDetailsList = lateFeeDetailsRepository.findLateFeesByLoanId(loanDetails.getLoanId());
        for (LateFeeDetails lateFeeDetails : lateFeeDetailsList) {
            if (remainingAmount <= 0) break;
            if(lateFeeDetails.getPaid()==true){
                continue;
            }

            double feeAmount = lateFeeDetails.getLateFeeAmount();
            feeAmount -= lateFeeDetails.getPaidAmount();
            if (feeAmount > remainingAmount) {
                lateFeePaid += remainingAmount;
                feeAmount -= remainingAmount;
                remainingAmount = 0;
            } else {
                lateFeePaid += feeAmount;
                remainingAmount -= feeAmount;
                feeAmount = 0;
                lateFeeDetails.setPaid(true);
            }

            // Update the LateFeeDetails entry as paid
             // Assuming there's a field to mark as paid
            lateFeeDetails.setPaidAmount(lateFeeDetails.getPaidAmount() + lateFeePaid);
            lateFeeDetailsRepository.save(lateFeeDetails);
        }

        // 2. Pay interest on late fees (if any logic for this exists, assuming 0.1% per month for demonstration)
        double lateFeeInterest = lateFeePaid * 0.001; // Example interest calculation
        if (remainingAmount > 0) {
            if (lateFeeInterest > remainingAmount) {
                lateFeeInterestPaid = remainingAmount;
                remainingAmount = 0;
            } else {
                lateFeeInterestPaid = lateFeeInterest;
                remainingAmount -= lateFeeInterest;
            }
        }

        // 3. Pay accrued interest on the loan
        double accruedInterest = calculateInterest(loanDetails, request.getPaymentDate());
        if (remainingAmount > 0) {
            if (accruedInterest > remainingAmount) {
                accruedInterestPaid = remainingAmount;
                remainingAmount = 0;
            } else {
                accruedInterestPaid = accruedInterest;
                remainingAmount -= accruedInterest;
            }
        }

        // 4. Apply remaining amount towards principal
        if (remainingAmount > 0) {
            principalPaid = remainingAmount;
            if(principalPaid >=loanDetails.getOutstandingPrincipal()){
                loanDetails.setLoanStatus(LoanStatus.CLOSED);
            }
            remainingAmount = 0;
        }

        // Record the payment
        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(loanDetails);
        loanPayment.setPaymentDate(request.getPaymentDate());
        loanPayment.setPaymentAmount(paymentAmount);
        loanPayment.setLateFeeAmountPaid(lateFeePaid);
        loanPayment.setInterestPaid(accruedInterestPaid);
        loanPayment.setPrincipalPaid(principalPaid);
        loanPayment.setMissedEmiNumber(0);

        LoanPayment savedLoanPayment = loanPaymentRepository.save(loanPayment);
        //loanDetails.getPayments().add(loanPayment);
        // Update outstanding principal
        loanDetails.setOutstandingPrincipal(loanDetails.getOutstandingPrincipal() - principalPaid);
        loanDetailsRepository.save(loanDetails);

        // Log the transaction
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setLoanId(loanDetails.getLoanId());
        transactionHistory.setTransactionDate(request.getPaymentDate());
        transactionHistory.setTransactionType(TransactionType.PAYMENT);
        transactionHistory.setTransactionSubType(TransactionSubType.EMI_PAYMENT);
        transactionHistory.setAmount(paymentAmount);

        transactionHistoryRepository.save(transactionHistory);

        return loanPayment;
    }


    @Transactional(readOnly = true)
    public LoanDetails getLoanDetails(int loanId) {
        return loanDetailsRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    }

    private double calculateInterest(LoanDetails loanDetails, LocalDate paymentDate) {
        // Simplified interest calculation logic for demonstration
        // Implement actual interest calculation based on loan details and payment date
        return loanDetails.getOutstandingPrincipal() * (loanDetails.getInterestRate() / 100 / 12);
    }
}
