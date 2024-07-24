package com.megaloans.lms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanCreationRequest {
    private int userId;
    private int mutualFundId;
    private double loanAmount;
    private double interestRate;
    private int loanTerm; // in months
    private LocalDate disbursementDate;
}
