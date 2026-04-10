package com.nextstep.backend.controllers;

import com.nextstep.backend.models.Perfil;
import com.nextstep.backend.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    @Autowired
    private PerfilRepository perfilRepository;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<Perfil>> getPerfil(@PathVariable String idUsuario) {
        Optional<Perfil> perfilOpt = perfilRepository.findByIdUsuario(idUsuario);
        
        if (perfilOpt.isPresent()) {
            return ResponseEntity.ok(List.of(perfilOpt.get()));
        } else {
            Perfil novoPerfil = new Perfil();
            novoPerfil.setIdUsuario(idUsuario);
            novoPerfil.setFullName("Usuário NextStep");
            return ResponseEntity.ok(List.of(perfilRepository.save(novoPerfil)));
        }
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<List<Perfil>> updatePerfil(@PathVariable String idUsuario, @RequestBody Perfil perfilAtualizado) {
        Optional<Perfil> perfilOpt = perfilRepository.findByIdUsuario(idUsuario);
        Perfil perfil;
        
        if (perfilOpt.isPresent()) {
            perfil = perfilOpt.get();
        } else {
            perfil = new Perfil();
            perfil.setIdUsuario(idUsuario);
        }

        perfil.setFullName(perfilAtualizado.getFullName());
        perfil.setCompanyName(perfilAtualizado.getCompanyName());
        
        if (perfilAtualizado.getAvatar() != null) {
            perfil.setAvatar(perfilAtualizado.getAvatar());
        }

        Perfil salvo = perfilRepository.save(perfil);
        return ResponseEntity.ok(List.of(salvo));
    }
}