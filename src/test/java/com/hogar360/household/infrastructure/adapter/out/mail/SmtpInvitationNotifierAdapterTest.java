package com.hogar360.household.infrastructure.adapter.out.mail;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmtpInvitationNotifierAdapterTest {

    @SuppressWarnings("unchecked")
    private final ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
    private final JavaMailSender sender = mock(JavaMailSender.class);

    @Test
    void conCorreoActivadoEnviaElMensajeAlInvitado() {
        when(provider.getIfAvailable()).thenReturn(sender);
        var adapter = new SmtpInvitationNotifierAdapter(provider, true, "hogar@mail.com", "http://localhost:4200");

        adapter.notifyInvitation("luis@mail.com", "Casa Pérez", "ana@mail.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        assertArrayEquals(new String[]{"luis@mail.com"}, captor.getValue().getTo());
        assertTrue(captor.getValue().getText().contains("Casa Pérez"));
        assertTrue(captor.getValue().getText().contains("ana@mail.com"));
    }

    @Test
    void conCorreoDesactivadoNoEnviaNada() {
        var adapter = new SmtpInvitationNotifierAdapter(provider, false, "", "http://localhost:4200");

        adapter.notifyInvitation("luis@mail.com", "Casa", "ana@mail.com");

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }
}
