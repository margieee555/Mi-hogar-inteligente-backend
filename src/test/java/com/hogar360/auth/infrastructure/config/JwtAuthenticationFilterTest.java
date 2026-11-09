package com.hogar360.auth.infrastructure.config;

import com.hogar360.auth.infrastructure.adapter.out.security.JwtTokenAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenAdapter jwtTokenAdapter;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void conTokenValido_autenticaAlUsuarioYContinuaLaCadena() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockFilterChain chain = new MockFilterChain();

        when(jwtTokenAdapter.isValid("token-valido")).thenReturn(true);
        when(jwtTokenAdapter.extractEmail("token-valido")).thenReturn("juan@mail.com");
        when(jwtTokenAdapter.extractRole("token-valido")).thenReturn("USER");

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("juan@mail.com", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertNotNull(chain.getRequest()); // la cadena continuó
    }

    @Test
    void conTokenInvalido_noAutenticaPeroContinuaLaCadena() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-malo");
        MockFilterChain chain = new MockFilterChain();

        when(jwtTokenAdapter.isValid("token-malo")).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(chain.getRequest());
        verify(jwtTokenAdapter, never()).extractEmail(any());
    }

    @Test
    void sinHeaderAuthorization_noAutentica() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(new MockHttpServletRequest(), response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(chain.getRequest());
        verifyNoInteractions(jwtTokenAdapter);
    }

    @Test
    void conHeaderSinPrefijoBearer_noAutentica() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtTokenAdapter);
    }
}