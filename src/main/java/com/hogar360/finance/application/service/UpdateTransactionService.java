package com.hogar360.finance.application.service;

import com.hogar360.finance.application.dto.UpdateTransactionCommand;
import com.hogar360.finance.domain.exception.TransactionNotFoundException;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.port.in.UpdateTransactionUseCase;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateTransactionService implements UpdateTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final FinanceHouseholdResolver householdResolver;

    public UpdateTransactionService(TransactionRepositoryPort repository, FinanceHouseholdResolver householdResolver) {
        this.repository = repository;
        this.householdResolver = householdResolver;
    }

    @Override
    public Transaction update(UpdateTransactionCommand command) {
        Long householdId = householdResolver.requireHouseholdId(command.ownerEmail());

        Transaction existing = repository.findByIdAndHouseholdId(command.transactionId(), householdId)
                .orElseThrow(() -> new TransactionNotFoundException(command.transactionId()));

        Transaction updated = existing.update(command.type(), command.category(), command.amount(),
                command.description(), command.date());

        return repository.save(updated);
    }
}