package com.nextstep.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.CategoriaDTO;
import com.nextstep.backend.models.Categoria;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Método auxiliar para pegar o Utilizador que está logado (através do Token JWT)
    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // -----------------------------------------------------
    // 1. CRIAR UMA NOVA CATEGORIA
    // -----------------------------------------------------
    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody CategoriaDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();

        Categoria novaCategoria = new Categoria();
        novaCategoria.setName(data.name());
        novaCategoria.setType(data.type());
        novaCategoria.setUsuario(usuarioLogado); // Vincula a categoria ao dono do Token!

        Categoria categoriaSalva = categoriaRepository.save(novaCategoria);
        return ResponseEntity.ok(categoriaSalva);
    }

    // -----------------------------------------------------
    // 2. LISTAR AS CATEGORIAS DO UTILIZADOR
    // -----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        Usuario usuarioLogado = getUsuarioLogado();
        
        // Vai buscar à base de dados apenas as categorias deste utilizador
        List<Categoria> minhasCategorias = categoriaRepository.findByUsuarioId(usuarioLogado.getId());
        
        return ResponseEntity.ok(minhasCategorias);
    }
}