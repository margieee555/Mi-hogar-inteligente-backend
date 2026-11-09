package com.hogar360.auth.domain.model;

/** Datos verificados que Google certifica sobre una persona. */
public record GoogleIdentity(String subject, String email, String name) {
}