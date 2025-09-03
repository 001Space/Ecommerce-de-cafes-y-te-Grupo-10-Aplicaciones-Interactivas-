package com.tpo.cafe.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tpo.cafe.dominios.Producto;

import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
  Page<Producto> findByCategory_IdAndActivoTrue(Long categoryId, Pageable pageable);
  List<Producto> findBySellerId(Long sellerId);

  // si ya tenías una query compleja, dejá esta firma y ajustá tu @Query
  default Page<Producto> searchPublic(Long categoryId, String q, Double min, Double max, Pageable pageable) {
    // versión mínima: por categoría (podés mejorarla con @Query)
    if (categoryId != null) return findByCategory_IdAndActivoTrue(categoryId, pageable);
    return findAll(pageable);
  }
}
