package com.nextstep.backend.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nextstep.backend.models.Usuario;
import org.springframework.beans.factory.annotation.Value;

// @Service avisa o Spring que esta classe contém lógica de negócio
@Service
public class TokenService {

    /// O segredo agora é injetado com segurança pelo application.properties
    @Value("${jwt.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            // Usamos o algoritmo HMAC256 com a nossa chave mestre para assinar o token
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("nextstep-api")
                    .withSubject(usuario.getId()) 
                    .withClaim("email", usuario.getEmail()) 
                    .withExpiresAt(gerarDataExpiracao()) // Prazo de validade
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("nextstep-api")
                    .build()
                    .verify(token)
                    .getSubject(); // Se for válido, devolve o ID do usuário
        } catch (JWTVerificationException exception) {
            return ""; // Se for inválido ou expirado, devolve vazio
        }
    }

    private Instant gerarDataExpiracao() {
        // Validade ajustada para 24 horas (Padrão de segurança para plataformas financeiras)
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}