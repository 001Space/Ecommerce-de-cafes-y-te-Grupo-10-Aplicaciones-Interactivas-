package com.tpo.cafe.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpo.cafe.dominios.Carrito;
import com.tpo.cafe.dominios.EstadoCarrito;
import com.tpo.cafe.usuario.Usuario;

public interface CarritoRepositorio extends JpaRepository<Carrito, Long> {
  Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, EstadoCarrito estado);
}
