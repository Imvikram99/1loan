package com.megaloans.lms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.megaloans.lms.model.LoanPayment;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class LateFeeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lateFeeId;

    @OneToOne() // Inverse side, LoanPayment is the owner of this relationship
    @JsonBackReference
    @JoinColumn(name = "paymentId", unique = true)
    private LoanPayment loanPayment;

    private double lateFeeAmount;
    private LocalDate feeDate;

    private Boolean paid;
    private double paidAmount;

    @Override
    public String toString() {
        return "LateFeeDetails{" +
                "lateFeeId=" + lateFeeId +
                ", lateFeeAmount=" + lateFeeAmount +
                ", feeDate=" + feeDate +
                '}';
    }

}
