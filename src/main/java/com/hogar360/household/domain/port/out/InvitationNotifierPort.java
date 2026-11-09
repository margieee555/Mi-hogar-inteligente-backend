package com.hogar360.household.domain.port.out;

public interface InvitationNotifierPort {
    /** Avisa a la persona invitada. No debe lanzar excepciones: la invitación ya está guardada. */
    void notifyInvitation(String inviteeEmail, String householdName, String inviterEmail);
}