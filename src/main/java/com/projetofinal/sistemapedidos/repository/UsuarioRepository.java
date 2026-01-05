// ========== UsuarioRepository.java ==========
package com.projetofinal.sistemapedidos.repository;

import com.projetofinal.sistemapedidos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndPassword(String email, String password);
    Boolean existsByEmail(String email);
}