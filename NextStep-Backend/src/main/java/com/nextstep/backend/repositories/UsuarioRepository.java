package com.nextstep.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nextstep.backend.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    // O Spring monta a busca SQL automaticamente só pelo nome do método!
    Usuario findByEmail(String email);
}