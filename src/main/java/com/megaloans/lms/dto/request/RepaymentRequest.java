package com.megaloans.lms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RepaymentRequest {
    private int loanId;
    private double paymentAmount;
    private LocalDate paymentDate;
}
