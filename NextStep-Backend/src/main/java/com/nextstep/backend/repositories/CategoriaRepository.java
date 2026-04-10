package com.nextstep.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextstep.backend.models.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // O Spring cria a query SQL automaticamente: SELECT * FROM categorias WHERE usuario_id = ?
    List<Categoria> findByUsuarioId(String usuarioId);
}