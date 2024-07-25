package com.megaloans.lms.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class EMISchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int emiScheduleId;

    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanDetails loan;

    @Enumerated(EnumType.STRING)
    private PaymentFrequency paymentFrequency;

    private Integer paymentDay;

    private LocalDate startDate;

    private LocalDate endDate;

}