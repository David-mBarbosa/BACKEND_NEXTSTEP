package com.nextstep.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.TokenDTO;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.UsuarioRepository;
import com.nextstep.backend.services.TokenService;

@RestController
@RequestMapping("/auth") // Todas as rotas aqui vão começar com /auth
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // A nossa ferramenta de embaralhar senhas

    @Autowired
    private TokenService tokenService; // A nossa máquina de fazer crachás (JWT)

    // -----------------------------------------------------
    // 1. ROTA DE REGISTO (CRIAR CONTA)
    // -----------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody AuthDTO data) {
        // Verifica se já existe alguém com esse email
        if (usuarioRepository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().body("Erro: Email já está em uso!");
        }

        // Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(data.senha());
        
        // Cria o novo utilizador e salva no banco H2
        Usuario novoUsuario = new Usuario(data.email(), senhaCriptografada);
        usuarioRepository.save(novoUsuario);

        return ResponseEntity.ok("Utilizador criado com sucesso!");
    }

    // -----------------------------------------------------
    // 2. ROTA DE LOGIN
    // -----------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO data) {
        // Procura o utilizador no banco
        Usuario usuario = usuarioRepository.findByEmail(data.email());

        // Se o utilizador não existir ou a senha digitada não bater com a do banco
        if (usuario == null || !passwordEncoder.matches(data.senha(), usuario.getSenha())) {
            return ResponseEntity.status(401).body("Erro: Email ou senha incorretos.");
        }

        // Se a senha estiver correta, gera o Token JWT!
        String token = tokenService.gerarToken(usuario);

        // Devolve o token e os dados básicos para o Android/Angular guardarem
        return ResponseEntity.ok(new TokenDTO(token, usuario.getId(), usuario.getEmail()));
    }
}