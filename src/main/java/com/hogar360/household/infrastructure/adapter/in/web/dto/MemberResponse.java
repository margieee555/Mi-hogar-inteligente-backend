package com.hogar360.household.infrastructure.adapter.in.web.dto;

import com.hogar360.household.domain.model.MemberView;

import java.time.LocalDateTime;

public record MemberResponse(Long userId, String name, String email, String role, LocalDateTime joinedAt) {

    public static MemberResponse from(MemberView view) {
        return new MemberResponse(view.userId(), view.name(), view.email(), view.role().name(), view.joinedAt());
    }
}