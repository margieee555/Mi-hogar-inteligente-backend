package com.hogar360.auth.infrastructure.adapter.in.web;

import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.LoginUseCase;
import com.hogar360.auth.domain.port.in.RegisterUserUseCase;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import com.hogar360.auth.infrastructure.adapter.out.security.JwtTokenAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada HTTP. Solo traduce y delega en los casos de uso
 * (puertos "in"). No contiene lógica de negocio.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final AuthWebMapper mapper;
    private final JwtTokenAdapter jwtTokenAdapter;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                           LoginUseCase loginUseCase,
                           AuthWebMapper mapper,
                           JwtTokenAdapter jwtTokenAdapter) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.mapper = mapper;
        this.jwtTokenAdapter = jwtTokenAdapter;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUserUseCase.register(mapper.toCommand(request));
        String token = jwtTokenAdapter.generateToken(user);
        AuthResponse response = AuthResponse.of(token, user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.login(mapper.toCommand(request));
        User user = result.user();
        AuthResponse response = AuthResponse.of(result.token(), user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(response);
    }
}
