package com.hogar360.finance.domain.port.in;

public interface DeleteTransactionUseCase {
    void delete(Long transactionId, String ownerEmail);
}