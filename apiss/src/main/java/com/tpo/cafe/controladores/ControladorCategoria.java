package com.tpo.cafe.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpo.cafe.dominios.Categoria;
import com.tpo.cafe.repo.CategoriaRepositorio;

@RestController
@RequestMapping("/api/categorias")
public class ControladorCategoria {

  private final CategoriaRepositorio categorias;

  public ControladorCategoria(CategoriaRepositorio categorias) { this.categorias = categorias; }

  @GetMapping
  public List<Categoria> list() {
    return categorias.findAll();
  }

  @PostMapping
  public ResponseEntity<Categoria> create(@RequestBody Categoria c) {
    return ResponseEntity.ok(categorias.save(c));
  }
}
