package com.hogar360.finance.domain.port.out;

import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.model.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    /** Busca por id DENTRO del hogar: un movimiento de otro hogar se ve como inexistente. */
    Optional<Transaction> findByIdAndHouseholdId(Long id, Long householdId);

    /** Movimientos entre dos fechas (ambas inclusive); type null = ambos tipos. Más recientes primero. */
    List<Transaction> search(Long householdId, LocalDate from, LocalDate to, TransactionType type);

    void deleteById(Long id);
}