package com.nextstep.backend.repositories;

import com.nextstep.backend.models.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByIdUsuario(String idUsuario);
}