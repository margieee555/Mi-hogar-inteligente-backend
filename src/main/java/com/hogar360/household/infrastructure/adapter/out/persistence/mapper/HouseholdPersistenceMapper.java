package com.hogar360.household.infrastructure.adapter.out.persistence.mapper;

import com.hogar360.household.domain.model.Household;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.RoomEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HouseholdPersistenceMapper {

    public HouseholdEntity toEntity(Household household) {
        HouseholdEntity entity = new HouseholdEntity(
                household.getId(),
                household.getOwnerUserId(),
                household.getName(),
                household.getType(),
                household.getResidentCount(),
                household.isOnboardingCompleted(),
                household.getCreatedAt()
        );

        List<RoomEntity> roomEntities = household.getRooms().stream()
                .map(roomName -> new RoomEntity(entity, roomName))
                .toList();
        entity.replaceRooms(roomEntities);

        return entity;
    }

    public Household toDomain(HouseholdEntity entity) {
        List<String> roomNames = entity.getRooms().stream()
                .map(RoomEntity::getName)
                .toList();

        return new Household(
                entity.getId(),
                entity.getOwnerUserId(),
                entity.getName(),
                entity.getType(),
                entity.getResidentCount(),
                entity.isOnboardingCompleted(),
                roomNames,
                entity.getCreatedAt()
        );
    }
}