package com.nextstep.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.UsuarioRepository;
import com.nextstep.backend.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @Component diz ao Spring para criar e gerir esta classe automaticamente
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Este método é executado UMA VEZ em cada requisição que chega à API
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Pega o token do cabeçalho da requisição
        var token = this.recoverToken(request);

        // 2. Se houver um token, vamos validá-lo
        if (token != null) {
            var userId = tokenService.validarToken(token);
            
            // Se o token for válido (não expirou e a assinatura está correta), o userId não será vazio
            if (!userId.isEmpty()) {
                // Busca o utilizador no banco de dados para confirmar que ele ainda existe
                Usuario usuario = usuarioRepository.findById(userId).orElse(null);

                if (usuario != null) {
                    // Cria o objeto de autenticação do Spring
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, null);
                    
                    // Salva a autenticação no contexto atual (avisa o Spring: "Este utilizador está logado!")
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 3. Continua o fluxo da requisição (vai para o próximo filtro ou para o Controller)
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair o token do cabeçalho HTTP
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        
        // O padrão web é enviar o token assim: "Bearer eyJhbGciOiJIUzI1Ni..."
        // Então nós tiramos a palavra "Bearer " e devolvemos só o token limpo
        return authHeader.replace("Bearer ", "");
    }
}