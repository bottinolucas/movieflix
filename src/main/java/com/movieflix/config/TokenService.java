package com.movieflix.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.movieflix.entity.User;

@Component
public class TokenService {

    @Value("${movieflix.security.token.secret}")
    private String secret;

    @Value("${movieflix.security.token.salt}")
    private String salt;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret + salt);

            return JWT.create()
                    .withIssuer("movieflix")
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId())
                    .withClaim("userName", user.getName())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token JWT", exception);
        }
    }

    private Instant genExpirationDate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret + salt);

            DecodedJWT verify = JWT.require(algorithm)
                .withIssuer("movieflix")
                .build()
                .verify(token);
        
            JWTUserData jwtUserData = JWTUserData.builder()
                .id(verify.getClaim("userId").asLong())
                .name(verify.getClaim("userName").asString())
                .email(verify.getSubject())
                .build();

            return Optional.of(jwtUserData);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
        
    }
}
