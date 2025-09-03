package com.tpo.cafe.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpo.cafe.dominios.Categoria;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {}
