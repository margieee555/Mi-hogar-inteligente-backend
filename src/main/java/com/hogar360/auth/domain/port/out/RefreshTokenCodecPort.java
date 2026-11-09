package com.hogar360.auth.domain.port.out;

/** Genera refresh tokens aleatorios y calcula su hash para guardarlo. */
public interface RefreshTokenCodecPort {
    String generate();
    String hash(String rawToken);
}