package com.tpo.cafe.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tpo.cafe.dto.DtosAutenticacion.LoginReq;
import com.tpo.cafe.dto.DtosAutenticacion.RegisterReq;
import com.tpo.cafe.dto.DtosAutenticacion.TokenRes;
import com.tpo.cafe.service.ServicioAutenticacion;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class ControladorAutenticacion {

  private final ServicioAutenticacion service;

  public ControladorAutenticacion(ServicioAutenticacion service) { this.service = service; }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req) {
    service.register(req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<TokenRes> login(@Valid @RequestBody LoginReq req) {
    String token = service.login(req);
    return ResponseEntity.ok(new TokenRes(token));
  }
}
