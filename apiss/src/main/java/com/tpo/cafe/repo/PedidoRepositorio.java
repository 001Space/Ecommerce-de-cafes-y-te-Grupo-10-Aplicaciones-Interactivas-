package com.tpo.cafe.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpo.cafe.dominios.Pedido;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {}
