package com.megaloans.lms.controller;


import com.megaloans.lms.dto.request.LoanCreationRequest;
import com.megaloans.lms.dto.request.RepaymentRequest;
import com.megaloans.lms.model.LoanDetails;
import com.megaloans.lms.model.LoanPayment;
import com.megaloans.lms.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDetails> createLoan(@RequestBody LoanCreationRequest request) {
        LoanDetails loanDetails = loanService.createLoan(request);
        return ResponseEntity.ok(loanDetails);
    }

    @PostMapping("/repayments")
    public ResponseEntity<LoanPayment> makeRepayment(@RequestBody RepaymentRequest request) {
        LoanPayment loanPayment = loanService.makeRepayment(request);
        return ResponseEntity.ok(loanPayment);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDetails> getLoanDetails(@PathVariable int loanId) {
        LoanDetails loanDetails = loanService.getLoanDetails(loanId);
        return ResponseEntity.ok(loanDetails);
    }
}
