package com.hogar360.auth.infrastructure.adapter.in.web;

import com.hogar360.auth.domain.port.in.SetPasswordUseCase;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.SetPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Operaciones de la cuenta de la persona autenticada (authentication.getName() es el email del JWT). */
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final SetPasswordUseCase setPasswordUseCase;

    public AccountController(SetPasswordUseCase setPasswordUseCase) {
        this.setPasswordUseCase = setPasswordUseCase;
    }

    @PutMapping("/password")
    public ResponseEntity<Void> setPassword(@Valid @RequestBody SetPasswordRequest request,
                                            Authentication authentication) {
        setPasswordUseCase.setPassword(authentication.getName(), request.newPassword(), request.confirmPassword());
        return ResponseEntity.noContent().build();
    }
}