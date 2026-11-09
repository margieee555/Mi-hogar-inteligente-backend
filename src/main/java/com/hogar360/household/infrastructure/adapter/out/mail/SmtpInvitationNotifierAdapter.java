package com.hogar360.household.infrastructure.adapter.out.mail;

import com.hogar360.household.domain.port.out.InvitationNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SmtpInvitationNotifierAdapter implements InvitationNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpInvitationNotifierAdapter.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean enabled;
    private final String from;
    private final String frontendUrl;

    public SmtpInvitationNotifierAdapter(ObjectProvider<JavaMailSender> mailSender,
                                         @Value("${app.mail.enabled:false}") boolean enabled,
                                         @Value("${app.mail.from:}") String from,
                                         @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    @Override
    @Async
    public void notifyInvitation(String inviteeEmail, String householdName, String inviterEmail) {
        if (!enabled) {
            log.info("Correo desactivado (MAIL_ENABLED=false): no se envía la invitación a {}", inviteeEmail);
            return;
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("No hay JavaMailSender configurado; no se envía la invitación a {}", inviteeEmail);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (from != null && !from.isBlank()) {
                message.setFrom(from);
            }
            message.setTo(inviteeEmail);
            message.setSubject("Te invitaron a " + householdName + " en Hogar360 / You were invited to " + householdName);
            message.setText(buildBody(householdName, inviterEmail));
            sender.send(message);
            log.info("Invitación enviada a {}", inviteeEmail);
        } catch (Exception e) {
            log.error("No se pudo enviar la invitación a {}: {}", inviteeEmail, e.getMessage());
        }
    }

    private String buildBody(String householdName, String inviterEmail) {
        return "Hola,\n\n"
                + inviterEmail + " te invitó a unirte al hogar \"" + householdName + "\" en Hogar360.\n"
                + "Inicia sesión (o crea tu cuenta con este mismo correo) y acepta la invitación desde tu perfil:\n"
                + frontendUrl + "\n"
                + "La invitación vence en 7 días.\n\n"
                + "-----\n\n"
                + "Hi,\n\n"
                + inviterEmail + " invited you to join the household \"" + householdName + "\" on Hogar360.\n"
                + "Sign in (or sign up with this same email) and accept the invitation from your profile:\n"
                + frontendUrl + "\n"
                + "The invitation expires in 7 days.\n";
    }
}