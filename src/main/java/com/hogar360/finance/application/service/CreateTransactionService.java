package com.hogar360.finance.application.service;

import com.hogar360.finance.application.dto.CreateTransactionCommand;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.port.in.CreateTransactionUseCase;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionService implements CreateTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final FinanceHouseholdResolver householdResolver;

    public CreateTransactionService(TransactionRepositoryPort repository, FinanceHouseholdResolver householdResolver) {
        this.repository = repository;
        this.householdResolver = householdResolver;
    }

    @Override
    public Transaction create(CreateTransactionCommand command) {
        Long householdId = householdResolver.requireHouseholdId(command.ownerEmail());

        Transaction transaction = Transaction.createNew(householdId, command.type(), command.category(),
                command.amount(), command.description(), command.date());

        return repository.save(transaction);
    }
}