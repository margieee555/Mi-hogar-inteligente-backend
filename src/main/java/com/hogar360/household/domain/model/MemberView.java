package com.hogar360.household.domain.model;

import java.time.LocalDateTime;

public record MemberView(Long userId, String name, String email, MemberRole role, LocalDateTime joinedAt) {
}
