package com.hogar360.household.domain.port.in;

import com.hogar360.household.application.dto.InviteMemberCommand;

public interface InviteMemberUseCase {
    void invite(InviteMemberCommand command);
}
