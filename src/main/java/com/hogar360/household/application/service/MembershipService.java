package com.hogar360.household.application.service;

import com.hogar360.household.domain.exception.AlreadyInHouseholdException;
import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import com.hogar360.household.domain.exception.InvitationExpiredException;
import com.hogar360.household.domain.exception.InvitationNotFoundException;
import com.hogar360.household.domain.exception.InvitationNotPendingException;
import com.hogar360.household.domain.exception.MemberNotFoundException;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.HouseholdMember;
import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.domain.model.InvitationView;
import com.hogar360.household.domain.model.MemberRole;
import com.hogar360.household.domain.model.MemberView;
import com.hogar360.household.domain.model.UserSummary;
import com.hogar360.household.domain.port.in.ManageMembershipUseCase;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.household.domain.port.out.UserDirectoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MembershipService implements ManageMembershipUseCase {

    private final HouseholdMemberRepositoryPort memberRepository;
    private final InvitationRepositoryPort invitationRepository;
    private final HouseholdRepositoryPort householdRepository;
    private final UserLookupPort userLookupPort;
    private final UserDirectoryPort userDirectory;
    private final HouseholdOwnershipChecker ownershipChecker;

    public MembershipService(HouseholdMemberRepositoryPort memberRepository,
                             InvitationRepositoryPort invitationRepository,
                             HouseholdRepositoryPort householdRepository,
                             UserLookupPort userLookupPort,
                             UserDirectoryPort userDirectory,
                             HouseholdOwnershipChecker ownershipChecker) {
        this.memberRepository = memberRepository;
        this.invitationRepository = invitationRepository;
        this.householdRepository = householdRepository;
        this.userLookupPort = userLookupPort;
        this.userDirectory = userDirectory;
        this.ownershipChecker = ownershipChecker;
    }

    // ---- Miembros ----
    @Override
    public List<MemberView> listMembers(Long householdId, String requesterEmail) {
        requireMember(householdId, requesterEmail);

        List<HouseholdMember> members = memberRepository.findByHouseholdId(householdId);
        Map<Long, UserSummary> users = userDirectory.findByIds(members.stream().map(HouseholdMember::userId).toList())
                .stream().collect(Collectors.toMap(UserSummary::id, Function.identity(), (a, b) -> a));

        return members.stream()
                .map(m -> {
                    UserSummary user = users.get(m.userId());
                    return new MemberView(m.userId(), user != null ? user.name() : "",
                            user != null ? user.email() : "", m.role(), m.joinedAt());
                })
                .sorted(Comparator.comparing((MemberView v) -> v.role() != MemberRole.OWNER)
                        .thenComparing(MemberView::joinedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    @Transactional
    public void removeMember(Long householdId, Long memberUserId, String ownerEmail) {
        Household household = ownershipChecker.requireOwned(householdId, ownerEmail);
        if (household.isOwnedBy(memberUserId)) {
            throw new DomainValidationException("error.household.owner.cannotRemove",
                    "No puedes quitar al dueño del hogar");
        }
        memberRepository.findByHouseholdIdAndUserId(householdId, memberUserId)
                .orElseThrow(() -> new MemberNotFoundException(memberUserId));
        memberRepository.deleteByHouseholdIdAndUserId(householdId, memberUserId);
    }

    @Override
    @Transactional
    public void leave(Long householdId, String memberEmail) {
        Long userId = requireUserId(memberEmail);
        HouseholdMember member = memberRepository.findByHouseholdIdAndUserId(householdId, userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));
        if (member.role() == MemberRole.OWNER) {
            throw new DomainValidationException("error.household.owner.cannotLeave",
                    "El dueño no puede salir del hogar");
        }
        memberRepository.deleteByHouseholdIdAndUserId(householdId, userId);
    }

    // ---- Invitaciones del hogar (dueño) ----
    @Override
    public List<InvitationView> listHouseholdInvitations(Long householdId, String ownerEmail) {
        Household household = ownershipChecker.requireOwned(householdId, ownerEmail);
        LocalDateTime now = LocalDateTime.now();
        return invitationRepository.findPendingByHouseholdId(householdId).stream()
                .filter(i -> !i.isExpired(now))
                .map(i -> new InvitationView(i.getId(), householdId, household.getName(), "",
                        i.getEmail(), i.getCreatedAt(), i.getExpiresAt()))
                .toList();
    }

    @Override
    @Transactional
    public void cancelInvitation(Long householdId, Long invitationId, String ownerEmail) {
        ownershipChecker.requireOwned(householdId, ownerEmail);
        Invitation invitation = invitationRepository.findById(invitationId)
                .filter(i -> householdId.equals(i.getHouseholdId()))
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));
        invitationRepository.save(invitation.cancel());
    }

    // ---- Invitaciones recibidas (invitado) ----
    @Override
    public List<InvitationView> listMyInvitations(String email) {
        LocalDateTime now = LocalDateTime.now();
        List<InvitationView> views = new ArrayList<>();
        for (Invitation invitation : invitationRepository.findPendingByEmail(email.trim().toLowerCase())) {
            if (invitation.isExpired(now)) {
                continue;
            }
            Optional<Household> household = householdRepository.findById(invitation.getHouseholdId());
            if (household.isEmpty()) {
                continue;
            }
            String ownerName = userDirectory.findByIds(List.of(household.get().getOwnerUserId())).stream()
                    .findFirst().map(UserSummary::name).orElse("");
            views.add(new InvitationView(invitation.getId(), household.get().getId(), household.get().getName(),
                    ownerName, invitation.getEmail(), invitation.getCreatedAt(), invitation.getExpiresAt()));
        }
        return views;
    }

    @Override
    @Transactional
    public Household acceptInvitation(Long invitationId, String email) {
        Invitation invitation = requireOwnInvitation(invitationId, email);
        requireAnswerable(invitation);

        Long userId = requireUserId(email);
        releaseDraftHousehold(userId);

        memberRepository.save(new HouseholdMember(null, invitation.getHouseholdId(), userId,
                MemberRole.MEMBER, LocalDateTime.now()));
        invitationRepository.save(invitation.accept());

        return householdRepository.findById(invitation.getHouseholdId())
                .orElseThrow(() -> new HouseholdNotFoundException(invitation.getHouseholdId()));
    }

    @Override
    @Transactional
    public void declineInvitation(Long invitationId, String email) {
        Invitation invitation = requireOwnInvitation(invitationId, email);
        requireAnswerable(invitation);
        invitationRepository.save(invitation.decline());
    }

    /**
     * Quien ya pertenece a un hogar no puede aceptar, salvo que sea un hogar borrador suyo
     * (onboarding sin terminar y sin más miembros): ese borrador se elimina y se une al nuevo.
     */
    private void releaseDraftHousehold(Long userId) {
        Optional<HouseholdMember> current = memberRepository.findByUserId(userId);
        if (current.isEmpty()) {
            return;
        }
        HouseholdMember member = current.get();
        boolean draft = member.role() == MemberRole.OWNER
                && householdRepository.findById(member.householdId()).map(h -> !h.isOnboardingCompleted()).orElse(false)
                && memberRepository.findByHouseholdId(member.householdId()).size() == 1;
        if (!draft) {
            throw new AlreadyInHouseholdException();
        }
        householdRepository.deleteById(member.householdId());
    }

    // ---- Utilidades ----
    private Long requireUserId(String email) {
        return userLookupPort.findUserIdByEmail(email)
                .orElseThrow(() -> new HouseholdNotFoundException("No se encontró el usuario: " + email));
    }

    private void requireMember(Long householdId, String email) {
        Long userId = requireUserId(email);
        memberRepository.findByHouseholdIdAndUserId(householdId, userId)
                .orElseThrow(HouseholdAccessDeniedException::new);
    }

    /** Una invitación ajena se ve como inexistente. */
    private Invitation requireOwnInvitation(Long invitationId, String email) {
        return invitationRepository.findById(invitationId)
                .filter(i -> i.getEmail().equalsIgnoreCase(email.trim()))
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));
    }

    private void requireAnswerable(Invitation invitation) {
        if (invitation.getStatus() != Invitation.Status.PENDING) {
            throw new InvitationNotPendingException();
        }
        if (invitation.isExpired(LocalDateTime.now())) {
            throw new InvitationExpiredException();
        }
    }
}