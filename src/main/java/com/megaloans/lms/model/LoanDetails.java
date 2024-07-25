package com.megaloans.lms.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.megaloans.lms.model.LoanPayment;
import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class LoanDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int loanId;
    private int userId;
    private int mutualFundId;
    private double loanAmount;
    private double interestRate;
    private int loanTerm;
    private LocalDate disbursementDate;
    private LocalDate maturityDate;
    private double outstandingPrincipal;
    private double totalInterestAccrued;
    private double totalLateFees;
    private LoanStatus loanStatus;

    @OneToOne(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EMISchedule emiSchedule;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Inverse side, LoanPayment is the owner
    private List<LoanPayment> payments;
    @Version
    private Long version;

    @Override
    public String toString() {
        return "LoanDetails{" +
                "loanId=" + loanId +
                ", userId=" + userId +
                ", mutualFundId=" + mutualFundId +
                ", loanAmount=" + loanAmount +
                ", interestRate=" + interestRate +
                ", loanTerm=" + loanTerm +
                ", disbursementDate=" + disbursementDate +
                ", maturityDate=" + maturityDate +
                ", outstandingPrincipal=" + outstandingPrincipal +
                ", totalInterestAccrued=" + totalInterestAccrued +
                ", totalLateFees=" + totalLateFees +
                ", loanStatus=" + loanStatus +
                '}';
    }
}
