package com.hogar360.inventory.infrastructure.adapter.out.persistence.repository;

import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.infrastructure.adapter.out.persistence.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemEntity, Long> {

    Optional<InventoryItemEntity> findByIdAndHouseholdId(Long id, Long householdId);

    Optional<InventoryItemEntity> findFirstByHouseholdIdAndNameIgnoreCaseOrderByIdAsc(Long householdId, String name);

    @Query("""
            select i from InventoryItemEntity i
            where i.householdId = :householdId
            order by lower(i.name) asc, i.id asc
            """)
    List<InventoryItemEntity> findAllOfHousehold(@Param("householdId") Long householdId);

    @Query("""
            select i from InventoryItemEntity i
            where i.householdId = :householdId
              and i.category = :category
            order by lower(i.name) asc, i.id asc
            """)
    List<InventoryItemEntity> findByCategoryOfHousehold(@Param("householdId") Long householdId,
                                                        @Param("category") InventoryCategory category);
}