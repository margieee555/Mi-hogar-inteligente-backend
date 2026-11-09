package com.hogar360.inventory.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import com.hogar360.inventory.infrastructure.adapter.out.persistence.entity.InventoryItemEntity;
import com.hogar360.inventory.infrastructure.adapter.out.persistence.mapper.InventoryPersistenceMapper;
import com.hogar360.inventory.infrastructure.adapter.out.persistence.repository.InventoryItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final InventoryItemJpaRepository jpaRepository;
    private final InventoryPersistenceMapper mapper;

    public InventoryRepositoryAdapter(InventoryItemJpaRepository jpaRepository, InventoryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public InventoryItem save(InventoryItem item) {
        InventoryItemEntity saved = jpaRepository.save(mapper.toEntity(item));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<InventoryItem> findByIdAndHouseholdId(Long id, Long householdId) {
        return jpaRepository.findByIdAndHouseholdId(id, householdId).map(mapper::toDomain);
    }

    @Override
    public Optional<InventoryItem> findByHouseholdIdAndName(Long householdId, String name) {
        return jpaRepository.findFirstByHouseholdIdAndNameIgnoreCaseOrderByIdAsc(householdId, name)
                .map(mapper::toDomain);
    }

    @Override
    public List<InventoryItem> findByHousehold(Long householdId, InventoryCategory category) {
        List<InventoryItemEntity> entities = (category == null)
                ? jpaRepository.findAllOfHousehold(householdId)
                : jpaRepository.findByCategoryOfHousehold(householdId, category);

        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}