package com.hogar360.auth.infrastructure.adapter.out.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecureRefreshTokenCodecTest {

    private final SecureRefreshTokenCodec codec = new SecureRefreshTokenCodec();

    @Test
    void generate_producesDistinctUrlSafeTokens() {
        String a = codec.generate();
        String b = codec.generate();

        assertThat(a).isNotEqualTo(b);
        assertThat(a).matches("[A-Za-z0-9_-]{64}");
    }

    @Test
    void hash_isDeterministicAndHas64HexCharacters() {
        String hash = codec.hash("token");

        assertThat(hash).isEqualTo(codec.hash("token"));
        assertThat(hash).matches("[0-9a-f]{64}");
        assertThat(hash).isNotEqualTo(codec.hash("otro"));
    }
}