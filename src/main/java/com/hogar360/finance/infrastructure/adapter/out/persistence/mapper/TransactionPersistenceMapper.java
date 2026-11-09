package com.hogar360.finance.infrastructure.adapter.out.persistence.mapper;

import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionPersistenceMapper {

    public TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.getId(),
                transaction.getHouseholdId(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCreatedAt()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getHouseholdId(),
                entity.getType(),
                entity.getCategory(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate(),
                entity.getCreatedAt()
        );
    }
}