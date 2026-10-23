package org.home.backend.config;

import org.home.backend.security.JwtAudienceValidator;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAudienceValidatorTests {

    private final JwtAudienceValidator validator = new JwtAudienceValidator("hdh-api");

    @Test
    void acceptsTokenContainingConfiguredAudience() {
        assertThat(validator.validate(jwt(List.of("hdh-api", "another-api"))).hasErrors()).isFalse();
    }

    @Test
    void rejectsTokenWithoutConfiguredAudience() {
        assertThat(validator.validate(jwt(List.of("another-api"))).hasErrors()).isTrue();
    }

    @Test
    void validatesIssuerAndExpiration() {
        var issuerValidator = JwtValidators.createDefaultWithIssuer("https://issuer.example.com");

        assertThat(issuerValidator.validate(jwt(List.of("hdh-api"))).hasErrors()).isFalse();
        assertThat(issuerValidator.validate(jwt(List.of("hdh-api"), "https://other.example.com",
                Instant.now().plusSeconds(60))).hasErrors()).isTrue();
        assertThat(issuerValidator.validate(jwt(List.of("hdh-api"), "https://issuer.example.com",
                Instant.now().minusSeconds(300))).hasErrors()).isTrue();
    }

    private Jwt jwt(List<String> audience) {
        return jwt(audience, "https://issuer.example.com", Instant.now().plusSeconds(60));
    }

    private Jwt jwt(List<String> audience, String issuer, Instant expiresAt) {
        Instant now = Instant.now();
        Instant issuedAt = expiresAt.isBefore(now) ? expiresAt.minusSeconds(60) : now;
        return new Jwt("token", issuedAt, expiresAt, Map.of("alg", "none"),
                Map.of("iss", issuer, "aud", audience));
    }
}
