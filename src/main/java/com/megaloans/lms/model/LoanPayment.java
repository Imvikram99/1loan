package com.megaloans.lms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class LoanPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @ManyToOne()
    @JoinColumn(name = "loanId", nullable = false)
    @JsonBackReference
    private LoanDetails loan;

    @OneToOne(mappedBy = "loanPayment",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Prevents infinite recursion during JSON serialization
    private LateFeeDetails lateFeeDetails;


    private LocalDate paymentDate;
    private double paymentAmount;
    private double lateFeeAmountPaid;//this amount is used to pay latefees out of all payment recieved
    private double interestPaid; //this amount is used to pay interstpaid out of all payment recieved
    private double principalPaid;
    private int missedEmiNumber;

    @Override
    public String toString() {
        return "LoanPayment{" +
                "paymentId=" + paymentId +
                ", paymentAmount=" + paymentAmount +
                ", paymentDate=" + paymentDate +
                '}';
    }

    // Constructors, getters, and setters (omitted for brevity)
}
