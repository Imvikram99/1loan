package com.megaloans.lms.service;

import com.megaloans.lms.model.*;
import com.megaloans.lms.repository.LoanDetailsRepository;
import com.megaloans.lms.repository.LoanPaymentRepository;
import com.megaloans.lms.repository.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AccrualService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    LoanPaymentRepository loanPaymentRepository;

    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    @KafkaListener(topics = "emi-topic")
    public void processEMIMessage(String message) {
        //EMISchedule emiSchedule = objectMapper.readValue(message, EMISchedule.class);
        //calculateAndPostAccruals(emiSchedule);
    }

    @KafkaListener(topics = "retry-emi-topic")
    public void retryEMIMessage(String message) {
        //EMISchedule emiSchedule = objectMapper.readValue(message, EMISchedule.class);
        //calculateAndPostAccruals(emiSchedule);
        //need to update LoanPayment,LateFee, get both the message back modified loandetails and newLoandetails
        // need to make txnHistory status as Reversed
        //using fresh loan details object process again
        //add counter for retry as well after 5 we can add it for manual interventions, these will we extreme cases
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 5)
    private void calculateAndPostAccruals(EMISchedule emiSchedule) {
        // Calculate late fees and interest
        Double lateFee = calculateLateFee(emiSchedule);
        Double interest = calculateInterest(emiSchedule);
        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoan(emiSchedule.getLoan());
        //create a object of latefee
        loanPayment.setLateFeeDetails(new LateFeeDetails());
        loanPaymentRepository.save(loanPayment);
        emiSchedule.getLoan().setOutstandingPrincipal(emiSchedule.getLoan().getOutstandingPrincipal()+interest);
        emiSchedule.getLoan().setTotalInterestAccrued(emiSchedule.getLoan().getTotalInterestAccrued()+interest);
        emiSchedule.getLoan().setTotalLateFees(emiSchedule.getLoan().getTotalLateFees()+lateFee);


        // Create transaction history entries
        TransactionHistory lateFeeEntry = createTransactionHistoryEntry(emiSchedule, lateFee, TransactionType.ACCRUAL,TransactionSubType.LATE_FEE_PAYMENT);
        TransactionHistory interestEntry = createTransactionHistoryEntry(emiSchedule, interest, TransactionType.ACCRUAL,TransactionSubType.EMI_INTEREST);

        sendLoanDetailToKafka(emiSchedule.getLoan());
    }

    private void sendLoanDetailToKafka(LoanDetails loan) {
    }

    private Double calculateLateFee(EMISchedule emiSchedule) {
        // Implement late fee calculation logic here
        return null;
    }

    private Double calculateInterest(EMISchedule emiSchedule) {
        // Implement interest calculation logic here
        return null;
    }

    private TransactionHistory createTransactionHistoryEntry(EMISchedule emiSchedule, Double amount, TransactionType transactionType,TransactionSubType transactionSubType) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setLoanId(emiSchedule.getLoan().getLoanId());
        transactionHistory.setTransactionDate(LocalDate.now());
        transactionHistory.setTransactionType(transactionType);
        transactionHistory.setTransactionSubType(transactionSubType);
        transactionHistory.setAmount(amount);
        transactionHistoryRepository.save(transactionHistory);
        return transactionHistory;
    }
}