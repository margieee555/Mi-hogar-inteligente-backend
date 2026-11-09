package com.hogar360.shopping.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.shopping.domain.model.ShoppingItem;
import com.hogar360.shopping.domain.port.out.ShoppingRepositoryPort;
import com.hogar360.shopping.infrastructure.adapter.out.persistence.entity.ShoppingItemEntity;
import com.hogar360.shopping.infrastructure.adapter.out.persistence.mapper.ShoppingPersistenceMapper;
import com.hogar360.shopping.infrastructure.adapter.out.persistence.repository.ShoppingItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ShoppingRepositoryAdapter implements ShoppingRepositoryPort {

    private final ShoppingItemJpaRepository jpaRepository;
    private final ShoppingPersistenceMapper mapper;

    public ShoppingRepositoryAdapter(ShoppingItemJpaRepository jpaRepository, ShoppingPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ShoppingItem save(ShoppingItem item) {
        ShoppingItemEntity saved = jpaRepository.save(mapper.toEntity(item));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ShoppingItem> findByIdAndHouseholdId(Long id, Long householdId) {
        return jpaRepository.findByIdAndHouseholdId(id, householdId).map(mapper::toDomain);
    }

    @Override
    public List<ShoppingItem> findByHousehold(Long householdId) {
        return jpaRepository.findAllOfHousehold(householdId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public int deleteBoughtByHousehold(Long householdId) {
        return jpaRepository.deleteBoughtOfHousehold(householdId);
    }
}