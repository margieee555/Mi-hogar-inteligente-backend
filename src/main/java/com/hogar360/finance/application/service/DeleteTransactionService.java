package com.hogar360.finance.application.service;

import com.hogar360.finance.domain.exception.TransactionNotFoundException;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.port.in.DeleteTransactionUseCase;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteTransactionService implements DeleteTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final FinanceHouseholdResolver householdResolver;

    public DeleteTransactionService(TransactionRepositoryPort repository, FinanceHouseholdResolver householdResolver) {
        this.repository = repository;
        this.householdResolver = householdResolver;
    }

    @Override
    public void delete(Long transactionId, String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);

        Transaction existing = repository.findByIdAndHouseholdId(transactionId, householdId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        repository.deleteById(existing.getId());
    }
}