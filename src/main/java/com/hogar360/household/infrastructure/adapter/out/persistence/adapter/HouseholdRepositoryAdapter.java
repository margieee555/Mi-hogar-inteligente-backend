package com.hogar360.household.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdMemberEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.mapper.HouseholdPersistenceMapper;
import com.hogar360.household.infrastructure.adapter.out.persistence.repository.HouseholdJpaRepository;
import com.hogar360.household.infrastructure.adapter.out.persistence.repository.HouseholdMemberJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class HouseholdRepositoryAdapter implements HouseholdRepositoryPort {

    private final HouseholdJpaRepository jpaRepository;
    private final HouseholdPersistenceMapper mapper;
    private final HouseholdMemberJpaRepository memberJpaRepository;

    public HouseholdRepositoryAdapter(HouseholdJpaRepository jpaRepository,
                                      HouseholdPersistenceMapper mapper,
                                      HouseholdMemberJpaRepository memberJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.memberJpaRepository = memberJpaRepository;
    }

    /** Un hogar nuevo registra a su dueño como miembro OWNER en la misma transacción. */
    @Override
    @Transactional
    public Household save(Household household) {
        boolean isNew = household.getId() == null;
        HouseholdEntity saved = jpaRepository.save(mapper.toEntity(household));
        if (isNew) {
            memberJpaRepository.save(new HouseholdMemberEntity(
                    null, saved.getId(), saved.getOwnerUserId(), MemberRole.OWNER, LocalDateTime.now()));
        }
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Household> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Household> findByOwnerUserId(Long ownerUserId) {
        return jpaRepository.findByOwnerUserId(ownerUserId).map(mapper::toDomain);
    }

    @Override
    public boolean existsCompletedByOwnerUserId(Long ownerUserId) {
        return jpaRepository.existsByOwnerUserIdAndOnboardingCompletedTrue(ownerUserId);
    }

    @Override
    public Optional<Household> findByMemberUserId(Long userId) {
        return jpaRepository.findByMemberUserId(userId).map(mapper::toDomain);
    }

    @Override
    public boolean existsCompletedByMemberUserId(Long userId) {
        return jpaRepository.existsCompletedByMemberUserId(userId);
    }
    
        /** El flush obliga a borrar ya, para que el usuario pueda registrarse enseguida en otro hogar. */
    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
        jpaRepository.flush();
    }
}