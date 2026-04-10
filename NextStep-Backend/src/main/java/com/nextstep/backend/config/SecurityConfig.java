package com.nextstep.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration avisa que esta classe tem configurações para o Spring ler ao iniciar
@Configuration
// @EnableWebSecurity liga a personalização das regras de segurança
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // 1. Desabilita o CSRF: Como vamos usar Token (JWT), não precisamos dessa proteção padrão contra formulários falsos.
            .csrf(csrf -> csrf.disable())
            
            // 2. STATELESS: Avisamos que não vamos usar "sessão" tradicional. Toda requisição HTTP terá que trazer o Token!
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Regras de Acesso às Rotas
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/auth/**").permitAll() 
                .anyRequest().authenticated()
            )
            
            // 4. Exigência do H2 Database: permite que a tela dele seja renderizada dentro de um "iframe"
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            
            // 5. Diz ao Spring para executar o nosso filtro ANTES do filtro padrão de login
            .addFilterBefore(securityFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            
            .build();
    }

    // Configura o BCrypt como o embaralhador oficial de senhas do projeto
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    // Expõe a ferramenta de Autenticação do Spring para podermos chamá-la no nosso Controller depois
    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}