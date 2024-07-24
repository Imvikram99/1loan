package com.megaloans.lms.model;

public enum TransactionType {
  PAYMENT, 
  ACCRUAL, // Interest accrual on loan/late fee
  INTEREST, 
  ADJUSTMENT ,
  DISBURSEMENT
}

