package com.tpo.cafe.usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
  boolean existsByUsuario(String usuario);
  boolean existsByCorreo(String correo);
  Optional<Usuario> findByUsuario(String usuario);
  Optional<Usuario> findByCorreo(String correo);
  Optional<Usuario> findByUsuarioOrCorreo(String usuario, String correo);
}
