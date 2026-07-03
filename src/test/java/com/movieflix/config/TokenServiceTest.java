package com.movieflix.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.movieflix.entity.User;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret");
        ReflectionTestUtils.setField(tokenService, "salt", "test-salt");
    }

    @Test
    void generateToken_thenValidateToken_shouldRoundTripUserData() {
        User user = User.builder().id(1L).name("Lucas").email("lucas@teste.com").build();

        String token = tokenService.generateToken(user);
        Optional<JWTUserData> result = tokenService.validateToken(token);

        assertThat(token).isNotBlank();
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Lucas");
        assertThat(result.get().email()).isEqualTo("lucas@teste.com");
    }

    @Test
    void validateToken_shouldRejectTokenSignedWithDifferentSecret() {
        User user = User.builder().id(1L).name("Lucas").email("lucas@teste.com").build();
        String token = tokenService.generateToken(user);

        TokenService otherTokenService = new TokenService();
        ReflectionTestUtils.setField(otherTokenService, "secret", "outro-secret");
        ReflectionTestUtils.setField(otherTokenService, "salt", "outro-salt");

        assertThatThrownBy(() -> otherTokenService.validateToken(token))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validateToken_shouldRejectGarbageToken() {
        assertThatThrownBy(() -> tokenService.validateToken("token-invalido"))
                .isInstanceOf(RuntimeException.class);
    }
}
