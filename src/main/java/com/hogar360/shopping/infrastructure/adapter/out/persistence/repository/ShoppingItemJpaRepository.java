package com.hogar360.shopping.infrastructure.adapter.out.persistence.repository;

import com.hogar360.shopping.infrastructure.adapter.out.persistence.entity.ShoppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShoppingItemJpaRepository extends JpaRepository<ShoppingItemEntity, Long> {

    Optional<ShoppingItemEntity> findByIdAndHouseholdId(Long id, Long householdId);

    @Query("""
            select s from ShoppingItemEntity s
            where s.householdId = :householdId
            order by s.bought asc, lower(s.name) asc, s.id asc
            """)
    List<ShoppingItemEntity> findAllOfHousehold(@Param("householdId") Long householdId);

    @Modifying
    @Query("delete from ShoppingItemEntity s where s.householdId = :householdId and s.bought = true")
    int deleteBoughtOfHousehold(@Param("householdId") Long householdId);
}