package com.hogar360.auth.infrastructure.adapter.in.web;

import com.hogar360.auth.application.dto.LoginCommand;
import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.domain.exception.InvalidCredentialsException;
import com.hogar360.auth.domain.model.Role;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.LoginUseCase;
import com.hogar360.auth.domain.port.in.RegisterUserUseCase;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import com.hogar360.auth.infrastructure.adapter.out.security.JwtTokenAdapter;
import com.hogar360.household.domain.port.in.HasHouseholdUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Boot 3.4+: org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.hogar360.auth.domain.port.in.SessionUseCase;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import com.hogar360.auth.domain.exception.InvalidGoogleTokenException;
import com.hogar360.auth.domain.port.in.LoginWithGoogleUseCase;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final String URL = "/api/v1/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @MockBean private LoginUseCase loginUseCase;
    @MockBean private RegisterUserUseCase registerUserUseCase;
    @MockBean private AuthWebMapper mapper;
    @MockBean private JwtTokenAdapter jwtTokenAdapter;   // lo necesita también JwtAuthenticationFilter
    @MockBean private HasHouseholdUseCase hasHouseholdUseCase;
    @MockBean private SessionUseCase sessionUseCase;
    @MockBean private LoginWithGoogleUseCase loginWithGoogleUseCase;

    private User buildUser() {
        return new User(1L, "Juan", "juan@mail.com", "hash", Role.USER, LocalDateTime.now());
    }

    @Test
    void login_conDatosValidos_retorna200ConAuthResponse() throws Exception {
        when(mapper.toCommand(any(LoginRequest.class)))
                .thenReturn(new LoginCommand("juan@mail.com", "12345678"));
        when(loginUseCase.login(any(LoginCommand.class)))
                .thenReturn(new LoginResult("jwt-token", buildUser()));
        when(hasHouseholdUseCase.hasCompletedOnboarding(1L)).thenReturn(true);
        
        when(sessionUseCase.openSession(any())).thenReturn("refresh-token");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"juan@mail.com","password":"12345678"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.onboardingCompleted").value(true))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_token=refresh-token")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")));
    }

    @Test
    void login_conCredencialesInvalidas_retorna401() throws Exception {
        when(mapper.toCommand(any(LoginRequest.class)))
                .thenReturn(new LoginCommand("juan@mail.com", "mala"));
        when(loginUseCase.login(any(LoginCommand.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"juan@mail.com","password":"mala"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_conEmailInvalido_retorna400YNoLlamaAlCasoDeUso() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"no-es-un-correo","password":"1234"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loginUseCase);
    }

    @Test
    void login_conPasswordVacia_retorna400() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"juan@mail.com","password":""}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loginUseCase);
    }

    @Test
    void login_sinBody_retorna400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginWithGoogle_conTokenValido_retorna200ConCookieDeRefresh() throws Exception {
        when(loginWithGoogleUseCase.loginWithGoogle("id-token"))
                .thenReturn(new LoginResult("jwt-token", buildUser()));
        when(hasHouseholdUseCase.hasCompletedOnboarding(1L)).thenReturn(false);
        when(sessionUseCase.openSession(any())).thenReturn("refresh-token");

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken":"id-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.onboardingCompleted").value(false))
                .andExpect(header().string("Set-Cookie",
                        org.hamcrest.Matchers.containsString("refresh_token=refresh-token")));
    }

    @Test
    void loginWithGoogle_conTokenInvalido_retorna401() throws Exception {
        when(loginWithGoogleUseCase.loginWithGoogle("malo")).thenThrow(new InvalidGoogleTokenException());

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken":"malo"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithGoogle_sinIdToken_retorna400SinLlamarAlCasoDeUso() throws Exception {
        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken":""}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loginWithGoogleUseCase);
    }
}