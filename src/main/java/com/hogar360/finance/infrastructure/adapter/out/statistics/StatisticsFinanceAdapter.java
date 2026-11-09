package com.hogar360.finance.infrastructure.adapter.out.statistics;

import com.hogar360.finance.domain.model.TransactionType;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import com.hogar360.statistics.domain.model.MovementSnapshot;
import com.hogar360.statistics.domain.port.out.StatisticsFinancePort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StatisticsFinanceAdapter implements StatisticsFinancePort {

    private final TransactionRepositoryPort transactionRepository;

    public StatisticsFinanceAdapter(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<MovementSnapshot> findMovements(Long householdId, LocalDate from, LocalDate to) {
        return transactionRepository.search(householdId, from, to, null).stream()
                .map(t -> new MovementSnapshot(t.getType() == TransactionType.INCOME,
                        t.getCategory().name(), t.getAmount(), t.getDate()))
                .toList();
    }
}