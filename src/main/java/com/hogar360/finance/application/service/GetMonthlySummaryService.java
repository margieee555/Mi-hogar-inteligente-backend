package com.hogar360.finance.application.service;

import com.hogar360.finance.application.dto.MonthlySummaryQuery;
import com.hogar360.finance.domain.model.MonthlySummary;
import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.port.in.GetMonthlySummaryUseCase;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetMonthlySummaryService implements GetMonthlySummaryUseCase {

    private final TransactionRepositoryPort repository;
    private final FinanceHouseholdResolver householdResolver;

    public GetMonthlySummaryService(TransactionRepositoryPort repository, FinanceHouseholdResolver householdResolver) {
        this.repository = repository;
        this.householdResolver = householdResolver;
    }

    @Override
    public MonthlySummary getSummary(MonthlySummaryQuery query) {
        Long householdId = householdResolver.requireHouseholdId(query.ownerEmail());

        List<Transaction> monthTransactions = repository.search(
                householdId, query.period().atDay(1), query.period().atEndOfMonth(), null);

        return MonthlySummary.of(query.period(), monthTransactions);
    }
}