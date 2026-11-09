package com.hogar360.finance.domain.exception;

public class TransactionNotFoundException extends RuntimeException {

    private final Long transactionId;

    public TransactionNotFoundException(Long id) {
        super("No se encontró el movimiento con id: " + id);
        this.transactionId = id;
    }

    public Long getTransactionId() { return transactionId; }
}