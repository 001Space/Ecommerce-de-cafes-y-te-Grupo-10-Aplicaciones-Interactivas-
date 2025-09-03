package com.tpo.cafe.controladores;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.tpo.cafe.dominios.Producto;
import com.tpo.cafe.dto.DtosProducto.CreateReq;
import com.tpo.cafe.dto.DtosProducto.UpdateReq;
import com.tpo.cafe.service.ServicioProducto;
import com.tpo.cafe.usuario.UsuarioRepositorio;

@RestController
@RequestMapping("/api/productos")
public class ControladorProducto {

  private final ServicioProducto service;
  private final UsuarioRepositorio usuarios;

  public ControladorProducto(ServicioProducto service, UsuarioRepositorio usuarios) {
    this.service = service;
    this.usuarios = usuarios;
  }

  @GetMapping
  public Page<Producto> search(@RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String q,
                               @RequestParam(required = false) Double minPrice,
                               @RequestParam(required = false) Double maxPrice,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "12") int size) {
    return service.searchPublic(categoryId, q, minPrice, maxPrice, page, size);
  }

  @PostMapping
  public ResponseEntity<Producto> create(@AuthenticationPrincipal User principal,
                                         @RequestBody CreateReq req) {
    var seller = usuarios
        .findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    var p = service.create(seller.getId(), req);
    return ResponseEntity.ok(p);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Producto> update(@AuthenticationPrincipal User principal,
                                         @PathVariable Long id,
                                         @RequestBody UpdateReq req) {
    var seller = usuarios
        .findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    var p = service.update(id, seller.getId(), req);
    return ResponseEntity.ok(p);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@AuthenticationPrincipal User principal,
                                  @PathVariable Long id) {
    var seller = usuarios
        .findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    service.delete(id, seller.getId());
    return ResponseEntity.noContent().build();
  }
}
