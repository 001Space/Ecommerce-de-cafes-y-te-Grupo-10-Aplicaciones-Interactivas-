package com.tpo.cafe.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.tpo.cafe.dto.DtosCarrito.AddItemReq;
import com.tpo.cafe.dto.DtosCarrito.UpdateItemReq;
import com.tpo.cafe.service.ServicioCarrito;
import com.tpo.cafe.usuario.UsuarioRepositorio;

@RestController
@RequestMapping("/api/cart")
public class ControladorCarrito {

  private final ServicioCarrito service;
  private final UsuarioRepositorio usuarios;

  public ControladorCarrito(ServicioCarrito service, UsuarioRepositorio usuarios) {
    this.service = service;
    this.usuarios = usuarios;
  }

  @GetMapping
  public ResponseEntity<?> get(@AuthenticationPrincipal User principal) {
    var u = usuarios.findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return ResponseEntity.ok(service.getOpen(u));
  }

  @PostMapping("/items")
  public ResponseEntity<?> addItem(@AuthenticationPrincipal User principal, @RequestBody AddItemReq req) {
    var u = usuarios.findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return ResponseEntity.ok(service.addItem(u, req));
  }

  @PutMapping("/items/{itemId}")
  public ResponseEntity<?> updateItem(@AuthenticationPrincipal User principal,
                                      @PathVariable Long itemId,
                                      @RequestBody UpdateItemReq req) {
    var u = usuarios.findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return ResponseEntity.ok(service.updateItem(u, itemId, req));
  }

  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<?> removeItem(@AuthenticationPrincipal User principal, @PathVariable Long itemId) {
    var u = usuarios.findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return ResponseEntity.ok(service.removeItem(u, itemId));
  }

  @PostMapping("/checkout")
  public ResponseEntity<?> checkout(@AuthenticationPrincipal User principal) {
    var u = usuarios.findByUsuarioOrCorreo(principal.getUsername(), principal.getUsername())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return ResponseEntity.ok(service.checkout(u));
  }
}
