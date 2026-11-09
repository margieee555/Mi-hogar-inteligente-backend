package com.hogar360.household.infrastructure.adapter.out.persistence.repository;

import com.hogar360.household.infrastructure.adapter.out.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {
}
