package com.hogar360.finance.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.model.TransactionType;
import com.hogar360.finance.domain.port.out.TransactionRepositoryPort;
import com.hogar360.finance.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import com.hogar360.finance.infrastructure.adapter.out.persistence.mapper.TransactionPersistenceMapper;
import com.hogar360.finance.infrastructure.adapter.out.persistence.repository.TransactionJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository, TransactionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity saved = jpaRepository.save(mapper.toEntity(transaction));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findByIdAndHouseholdId(Long id, Long householdId) {
        return jpaRepository.findByIdAndHouseholdId(id, householdId).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> search(Long householdId, LocalDate from, LocalDate to, TransactionType type) {
        List<TransactionEntity> entities = (type == null)
                ? jpaRepository.searchAll(householdId, from, to)
                : jpaRepository.searchByType(householdId, type, from, to);

        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}