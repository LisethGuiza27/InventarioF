package com.inventario.repository;

import com.inventario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByRolId(Integer rolId);

    @Query("SELECT u FROM Usuario u WHERE u.bloqueadoHasta < :fecha")
    List<Usuario> findUsuariosBloqueadosExpirados(LocalDateTime fecha);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.rol WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRol(String username);
}
