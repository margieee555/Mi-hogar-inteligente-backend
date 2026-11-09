package com.hogar360.household.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdMemberEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.mapper.HouseholdPersistenceMapper;
import com.hogar360.household.infrastructure.adapter.out.persistence.repository.HouseholdJpaRepository;
import com.hogar360.household.infrastructure.adapter.out.persistence.repository.HouseholdMemberJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HouseholdRepositoryAdapterTest {

    @Mock private HouseholdJpaRepository jpaRepository;
    @Mock private HouseholdMemberJpaRepository memberJpaRepository;

    private HouseholdRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new HouseholdRepositoryAdapter(jpaRepository, new HouseholdPersistenceMapper(), memberJpaRepository);
    }

    @Test
    void alCrearUnHogarNuevoRegistraAlDuenoComoMiembroOwner() {
        HouseholdEntity guardado = new HouseholdEntity(10L, 5L, "Casa", HomeType.CASA, 2, false, LocalDateTime.now());
        when(jpaRepository.save(any(HouseholdEntity.class))).thenReturn(guardado);

        adapter.save(Household.createNew(5L, "Casa", HomeType.CASA, 2));

        ArgumentCaptor<HouseholdMemberEntity> captor = ArgumentCaptor.forClass(HouseholdMemberEntity.class);
        verify(memberJpaRepository).save(captor.capture());
        assertEquals(10L, captor.getValue().getHouseholdId());
        assertEquals(5L, captor.getValue().getUserId());
        assertEquals(MemberRole.OWNER, captor.getValue().getRole());
    }

    @Test
    void alActualizarUnHogarExistenteNoDuplicaMiembros() {
        HouseholdEntity guardado = new HouseholdEntity(10L, 5L, "Casa", HomeType.CASA, 3, false, LocalDateTime.now());
        when(jpaRepository.save(any(HouseholdEntity.class))).thenReturn(guardado);
        Household existente = new Household(10L, 5L, "Casa", HomeType.CASA, 3, false, List.of(), LocalDateTime.now());

        adapter.save(existente);

        verifyNoInteractions(memberJpaRepository);
    }
}