package com.hogar360.household.application.service;

import com.hogar360.household.domain.exception.AlreadyInHouseholdException;
import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.exception.InvitationExpiredException;
import com.hogar360.household.domain.exception.InvitationNotFoundException;
import com.hogar360.household.domain.exception.InvitationNotPendingException;
import com.hogar360.household.domain.exception.MemberNotFoundException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.HouseholdMember;
import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.domain.model.InvitationView;
import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.domain.model.MemberView;
import com.hogar360.household.domain.model.UserSummary;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.household.domain.port.out.UserDirectoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembershipServiceTest {

    private static final String OWNER = "ana@mail.com";
    private static final String LUIS = "luis@mail.com";

    @Mock private HouseholdMemberRepositoryPort memberRepository;
    @Mock private InvitationRepositoryPort invitationRepository;
    @Mock private HouseholdRepositoryPort householdRepository;
    @Mock private UserLookupPort userLookup;
    @Mock private UserDirectoryPort userDirectory;
    @Mock private HouseholdOwnershipChecker ownershipChecker;

    private MembershipService service;
    private Household hogar;
    private HouseholdMember ownerMember;
    private HouseholdMember luisMember;

    @BeforeEach
    void setUp() {
        service = new MembershipService(memberRepository, invitationRepository, householdRepository,
                userLookup, userDirectory, ownershipChecker);

        LocalDateTime ahora = LocalDateTime.now();
        hogar = new Household(10L, 1L, "Casa Pérez", HomeType.CASA, 3, true, List.of(), ahora);
        ownerMember = new HouseholdMember(100L, 10L, 1L, MemberRole.OWNER, ahora.minusDays(5));
        luisMember = new HouseholdMember(101L, 10L, 7L, MemberRole.MEMBER, ahora.minusDays(1));

        when(userLookup.findUserIdByEmail(OWNER)).thenReturn(Optional.of(1L));
        when(userLookup.findUserIdByEmail(LUIS)).thenReturn(Optional.of(7L));
        when(ownershipChecker.requireOwned(10L, OWNER)).thenReturn(hogar);
        when(householdRepository.findById(10L)).thenReturn(Optional.of(hogar));
    }

    private Invitation pendiente(Long id, String email, LocalDateTime vence) {
        return new Invitation(id, 10L, email, Invitation.Status.PENDING, LocalDateTime.now().minusDays(1), vence);
    }

    // ---- Miembros ----
    @Test
    void listaLosMiembrosConNombreYElDuenoPrimero() {
        when(memberRepository.findByHouseholdIdAndUserId(10L, 1L)).thenReturn(Optional.of(ownerMember));
        when(memberRepository.findByHouseholdId(10L)).thenReturn(List.of(luisMember, ownerMember));
        when(userDirectory.findByIds(any())).thenReturn(List.of(
                new UserSummary(1L, "Ana", OWNER), new UserSummary(7L, "Luis", LUIS)));

        List<MemberView> members = service.listMembers(10L, OWNER);

        assertEquals(2, members.size());
        assertEquals(MemberRole.OWNER, members.get(0).role());
        assertEquals("Ana", members.get(0).name());
        assertEquals("Luis", members.get(1).name());
    }

    @Test
    void quienNoEsMiembroNoPuedeVerLaLista() {
        when(memberRepository.findByHouseholdIdAndUserId(10L, 7L)).thenReturn(Optional.empty());

        assertThrows(HouseholdAccessDeniedException.class, () -> service.listMembers(10L, LUIS));
    }

    @Test
    void elDuenoQuitaAUnMiembro() {
        when(memberRepository.findByHouseholdIdAndUserId(10L, 7L)).thenReturn(Optional.of(luisMember));

        service.removeMember(10L, 7L, OWNER);

        verify(memberRepository).deleteByHouseholdIdAndUserId(10L, 7L);
    }

    @Test
    void elDuenoNoPuedeQuitarseASiMismo() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.removeMember(10L, 1L, OWNER));

        assertEquals("error.household.owner.cannotRemove", ex.getCode());
        verify(memberRepository, never()).deleteByHouseholdIdAndUserId(any(), any());
    }

    @Test
    void quitarAQuienNoEsMiembroDaNoEncontrado() {
        when(memberRepository.findByHouseholdIdAndUserId(10L, 99L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> service.removeMember(10L, 99L, OWNER));
    }

    @Test
    void unMiembroPuedeSalirDelHogar() {
        when(memberRepository.findByHouseholdIdAndUserId(10L, 7L)).thenReturn(Optional.of(luisMember));

        service.leave(10L, LUIS);

        verify(memberRepository).deleteByHouseholdIdAndUserId(10L, 7L);
    }

    @Test
    void elDuenoNoPuedeSalirDelHogar() {
        when(memberRepository.findByHouseholdIdAndUserId(10L, 1L)).thenReturn(Optional.of(ownerMember));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> service.leave(10L, OWNER));

        assertEquals("error.household.owner.cannotLeave", ex.getCode());
        verify(memberRepository, never()).deleteByHouseholdIdAndUserId(any(), any());
    }

    // ---- Invitaciones del hogar ----
    @Test
    void elDuenoVeSoloLasInvitacionesVigentes() {
        LocalDateTime ahora = LocalDateTime.now();
        when(invitationRepository.findPendingByHouseholdId(10L)).thenReturn(List.of(
                pendiente(5L, LUIS, ahora.plusDays(3)),
                pendiente(6L, "vencida@mail.com", ahora.minusDays(1))));

        List<InvitationView> views = service.listHouseholdInvitations(10L, OWNER);

        assertEquals(1, views.size());
        assertEquals(LUIS, views.get(0).email());
    }

    @Test
    void elDuenoCancelaUnaInvitacion() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));

        service.cancelInvitation(10L, 5L, OWNER);

        ArgumentCaptor<Invitation> captor = ArgumentCaptor.forClass(Invitation.class);
        verify(invitationRepository).save(captor.capture());
        assertEquals(Invitation.Status.CANCELLED, captor.getValue().getStatus());
    }

    @Test
    void noSePuedeCancelarLaInvitacionDeOtroHogar() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(
                new Invitation(5L, 99L, "x@mail.com", Invitation.Status.PENDING,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(3))));

        assertThrows(InvitationNotFoundException.class, () -> service.cancelInvitation(10L, 5L, OWNER));

        verify(invitationRepository, never()).save(any());
    }

    // ---- Invitaciones recibidas ----
    @Test
    void listaMisInvitacionesVigentesConHogarYNombreDelDueno() {
        LocalDateTime ahora = LocalDateTime.now();
        when(invitationRepository.findPendingByEmail(LUIS)).thenReturn(List.of(
                pendiente(5L, LUIS, ahora.plusDays(3)),
                pendiente(6L, LUIS, ahora.minusDays(1))));
        when(userDirectory.findByIds(any())).thenReturn(List.of(new UserSummary(1L, "Ana", OWNER)));

        List<InvitationView> views = service.listMyInvitations("Luis@Mail.com");

        assertEquals(1, views.size());
        assertEquals("Casa Pérez", views.get(0).householdName());
        assertEquals("Ana", views.get(0).ownerName());
    }

    @Test
    void aceptarCreaLaMembresiaYMarcaLaInvitacionComoAceptada() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));
        when(memberRepository.findByUserId(7L)).thenReturn(Optional.empty());

        Household resultado = service.acceptInvitation(5L, LUIS);

        ArgumentCaptor<HouseholdMember> miembro = ArgumentCaptor.forClass(HouseholdMember.class);
        verify(memberRepository).save(miembro.capture());
        assertEquals(10L, miembro.getValue().householdId());
        assertEquals(7L, miembro.getValue().userId());
        assertEquals(MemberRole.MEMBER, miembro.getValue().role());

        ArgumentCaptor<Invitation> invitacion = ArgumentCaptor.forClass(Invitation.class);
        verify(invitationRepository).save(invitacion.capture());
        assertEquals(Invitation.Status.ACCEPTED, invitacion.getValue().getStatus());
        assertEquals(10L, resultado.getId());
    }

    @Test
    void unaInvitacionAjenaSeVeComoInexistente() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));

        assertThrows(InvitationNotFoundException.class, () -> service.acceptInvitation(5L, "otro@mail.com"));

        verify(memberRepository, never()).save(any());
    }

    @Test
    void noPuedeAceptarQuienYaPerteneceAUnHogar() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));
        when(memberRepository.findByUserId(7L)).thenReturn(Optional.of(luisMember));

        assertThrows(AlreadyInHouseholdException.class, () -> service.acceptInvitation(5L, LUIS));

        verify(memberRepository, never()).save(any());
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void noSePuedeAceptarUnaInvitacionVencidaNiYaRespondida() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().minusDays(1))));
        assertThrows(InvitationExpiredException.class, () -> service.acceptInvitation(5L, LUIS));

        when(invitationRepository.findById(6L)).thenReturn(Optional.of(
                new Invitation(6L, 10L, LUIS, Invitation.Status.ACCEPTED,
                        LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(3))));
        assertThrows(InvitationNotPendingException.class, () -> service.acceptInvitation(6L, LUIS));
    }

    @Test
    void rechazarMarcaLaInvitacionComoRechazada() {
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));

        service.declineInvitation(5L, LUIS);

        ArgumentCaptor<Invitation> captor = ArgumentCaptor.forClass(Invitation.class);
        verify(invitationRepository).save(captor.capture());
        assertEquals(Invitation.Status.DECLINED, captor.getValue().getStatus());
    }

        @Test
    void alAceptarSeDescartaElHogarBorradorDelInvitado() {
        HouseholdMember borrador = new HouseholdMember(200L, 20L, 7L, MemberRole.OWNER, LocalDateTime.now());
        Household hogarBorrador = new Household(20L, 7L, "Borrador", HomeType.CASA, 1, false, List.of(), LocalDateTime.now());
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));
        when(memberRepository.findByUserId(7L)).thenReturn(Optional.of(borrador));
        when(householdRepository.findById(20L)).thenReturn(Optional.of(hogarBorrador));
        when(memberRepository.findByHouseholdId(20L)).thenReturn(List.of(borrador));

        service.acceptInvitation(5L, LUIS);

        verify(householdRepository).deleteById(20L);
        verify(memberRepository).save(any(HouseholdMember.class));
    }

    @Test
    void unDuenoConHogarCompletoNoPuedeAceptarOtraInvitacion() {
        HouseholdMember dueno = new HouseholdMember(200L, 20L, 7L, MemberRole.OWNER, LocalDateTime.now());
        Household completo = new Household(20L, 7L, "Mi casa", HomeType.CASA, 2, true, List.of(), LocalDateTime.now());
        when(invitationRepository.findById(5L)).thenReturn(Optional.of(pendiente(5L, LUIS, LocalDateTime.now().plusDays(3))));
        when(memberRepository.findByUserId(7L)).thenReturn(Optional.of(dueno));
        when(householdRepository.findById(20L)).thenReturn(Optional.of(completo));
        when(memberRepository.findByHouseholdId(20L)).thenReturn(List.of(dueno));

        assertThrows(AlreadyInHouseholdException.class, () -> service.acceptInvitation(5L, LUIS));

        verify(householdRepository, never()).deleteById(any());
        verify(memberRepository, never()).save(any());
    }
}