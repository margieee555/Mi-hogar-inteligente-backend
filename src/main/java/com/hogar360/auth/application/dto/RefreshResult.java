package com.hogar360.auth.application.dto;

import com.hogar360.auth.domain.model.User;

public record RefreshResult(String accessToken, String refreshToken, User user) {
}