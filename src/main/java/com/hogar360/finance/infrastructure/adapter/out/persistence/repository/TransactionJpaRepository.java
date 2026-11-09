package com.hogar360.finance.infrastructure.adapter.out.persistence.repository;

import com.hogar360.finance.domain.model.TransactionType;
import com.hogar360.finance.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    Optional<TransactionEntity> findByIdAndHouseholdId(Long id, Long householdId);

    @Query("""
            select t from TransactionEntity t
            where t.householdId = :householdId
              and t.transactionDate between :startDate and :endDate
            order by t.transactionDate desc, t.id desc
            """)
    List<TransactionEntity> searchAll(@Param("householdId") Long householdId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("""
            select t from TransactionEntity t
            where t.householdId = :householdId
              and t.type = :type
              and t.transactionDate between :startDate and :endDate
            order by t.transactionDate desc, t.id desc
            """)
    List<TransactionEntity> searchByType(@Param("householdId") Long householdId,
                                         @Param("type") TransactionType type,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}