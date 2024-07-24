package com.megaloans.lms.model;

import javax.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class TransactionHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int transactionId;
  private int loanId;
  private LocalDate transactionDate;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;
  @Enumerated(EnumType.STRING)
  private TransactionSubType transactionSubType;
  private double amount;
}