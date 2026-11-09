package com.hogar360.auth.domain.port.in;

import com.hogar360.auth.application.dto.LoginResult;

public interface LoginWithGoogleUseCase {
    LoginResult loginWithGoogle(String idToken);
}