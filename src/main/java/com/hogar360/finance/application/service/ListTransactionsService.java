package com.hogar360.finance.application.service;

import com.hogar360.finance.application.dto.ListTransactionsQuery;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.port.in.ListTransactionsUseCase;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTransactionsService implements ListTransactionsUseCase {

    private final TransactionRepositoryPort repository;
    private final FinanceHouseholdResolver householdResolver;

    public ListTransactionsService(TransactionRepositoryPort repository, FinanceHouseholdResolver householdResolver) {
        this.repository = repository;
        this.householdResolver = householdResolver;
    }

    @Override
    public List<Transaction> list(ListTransactionsQuery query) {
        Long householdId = householdResolver.requireHouseholdId(query.ownerEmail());

        return repository.search(householdId, query.period().atDay(1), query.period().atEndOfMonth(), query.type());
    }
}