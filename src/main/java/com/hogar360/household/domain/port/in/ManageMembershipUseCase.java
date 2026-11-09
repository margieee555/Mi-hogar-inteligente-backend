package com.hogar360.household.domain.port.in;

import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.InvitationView;
import com.hogar360.household.domain.model.MemberView;

import java.util.List;

public interface ManageMembershipUseCase {

    /** Cualquier miembro del hogar puede ver quiénes lo integran. */
    List<MemberView> listMembers(Long householdId, String requesterEmail);

    /** Solo el dueño quita miembros (y no puede quitarse a sí mismo). */
    void removeMember(Long householdId, Long memberUserId, String ownerEmail);

    /** Un miembro sale del hogar; el dueño no puede. */
    void leave(Long householdId, String memberEmail);

    /** Solo el dueño: invitaciones pendientes y vigentes de su hogar. */
    List<InvitationView> listHouseholdInvitations(Long householdId, String ownerEmail);

    void cancelInvitation(Long householdId, Long invitationId, String ownerEmail);

    /** Invitaciones pendientes y vigentes dirigidas al correo del usuario autenticado. */
    List<InvitationView> listMyInvitations(String email);

    /** Acepta la invitación: el usuario pasa a ser miembro y se devuelve el hogar. */
    Household acceptInvitation(Long invitationId, String email);

    void declineInvitation(Long invitationId, String email);
}