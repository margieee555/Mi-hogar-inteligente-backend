package com.hogar360.auth.infrastructure.adapter.in.web;

import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.application.dto.RefreshResult;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.LoginUseCase;
import com.hogar360.auth.domain.port.in.LoginWithGoogleUseCase;
import com.hogar360.auth.domain.port.in.RegisterUserUseCase;
import com.hogar360.auth.domain.port.in.SessionUseCase;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.GoogleLoginRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import com.hogar360.auth.infrastructure.adapter.out.security.JwtTokenAdapter;
import com.hogar360.household.domain.port.in.HasHouseholdUseCase;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Adaptador de entrada HTTP. Solo traduce y delega en los casos de uso
 * (puertos "in"). No contiene lógica de negocio.
 *
 * El access token viaja en el cuerpo (el front lo guarda en memoria);
 * el refresh token viaja únicamente en una cookie HttpOnly.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";
    private static final String COOKIE_PATH = "/api/v1/auth";

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final AuthWebMapper mapper;
    private final JwtTokenAdapter jwtTokenAdapter;
    private final HasHouseholdUseCase hasHouseholdUseCase;
    private final SessionUseCase sessionUseCase;
    private final long refreshDays;
    private final boolean cookieSecure;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          LoginWithGoogleUseCase loginWithGoogleUseCase,
                          AuthWebMapper mapper,
                          JwtTokenAdapter jwtTokenAdapter,
                          HasHouseholdUseCase hasHouseholdUseCase,
                          SessionUseCase sessionUseCase,
                          @Value("${app.security.refresh-token-days:7}") long refreshDays,
                          @Value("${app.security.cookie-secure:false}") boolean cookieSecure) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.loginWithGoogleUseCase = loginWithGoogleUseCase;
        this.mapper = mapper;
        this.jwtTokenAdapter = jwtTokenAdapter;
        this.hasHouseholdUseCase = hasHouseholdUseCase;
        this.sessionUseCase = sessionUseCase;
        this.refreshDays = refreshDays;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUserUseCase.register(mapper.toCommand(request));
        String accessToken = jwtTokenAdapter.generateToken(user);
        // Usuario recién creado -> nunca ha completado el onboarding
        return sessionResponse(HttpStatus.CREATED, accessToken, user, false, sessionUseCase.openSession(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.login(mapper.toCommand(request));
        return authenticated(result);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        LoginResult result = loginWithGoogleUseCase.loginWithGoogle(request.idToken());
        return authenticated(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String rawRefreshToken) {
        RefreshResult result = sessionUseCase.refresh(rawRefreshToken);
        User user = result.user();
        boolean onboardingCompleted = hasHouseholdUseCase.hasCompletedOnboarding(user.getId());
        return sessionResponse(HttpStatus.OK, result.accessToken(), user, onboardingCompleted, result.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String rawRefreshToken) {
        sessionUseCase.closeSession(rawRefreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearedCookie().toString())
                .build();
    }

    /** Respuesta común de login con contraseña y login con Google. */
    private ResponseEntity<AuthResponse> authenticated(LoginResult result) {
        User user = result.user();
        boolean onboardingCompleted = hasHouseholdUseCase.hasCompletedOnboarding(user.getId());
        return sessionResponse(HttpStatus.OK, result.token(), user, onboardingCompleted, sessionUseCase.openSession(user));
    }

    private ResponseEntity<AuthResponse> sessionResponse(HttpStatus status, String accessToken, User user,
                                                         boolean onboardingCompleted, String refreshToken) {
        AuthResponse body = AuthResponse.of(accessToken, user.getName(), user.getEmail(),
                user.getRole().name(), onboardingCompleted, user.getAuthProvider().name());
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(refreshToken).toString())
                .body(body);
    }

    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(REFRESH_COOKIE, value == null ? "" : value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(Duration.ofDays(refreshDays))
                .build();
    }

    private ResponseCookie clearedCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}