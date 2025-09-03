package com.tpo.cafe.service;

import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpo.cafe.dto.DtosAutenticacion.LoginReq;
import com.tpo.cafe.dto.DtosAutenticacion.RegisterReq;
import com.tpo.cafe.seguridad.ServicioJwt;
import com.tpo.cafe.usuario.Rol;
import com.tpo.cafe.usuario.Usuario;
import com.tpo.cafe.usuario.UsuarioRepositorio;

@Service
@Transactional
public class ServicioAutenticacion {

  private final UsuarioRepositorio usuarios;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final ServicioJwt jwt;

  public ServicioAutenticacion(UsuarioRepositorio usuarios, PasswordEncoder encoder,
                               AuthenticationManager authManager, ServicioJwt jwt) {
    this.usuarios = usuarios;
    this.encoder = encoder;
    this.authManager = authManager;
    this.jwt = jwt;
  }

  public void register(RegisterReq req) {
    if (usuarios.existsByUsuario(req.getUsuario())) {
      throw new RuntimeException("El nombre de usuario ya existe");
    }
    if (usuarios.existsByCorreo(req.getCorreo())) {
      throw new RuntimeException("El correo ya existe");
    }

    Usuario u = new Usuario();
    u.setUsuario(req.getUsuario());
    u.setCorreo(req.getCorreo());
    u.setContrasenia(encoder.encode(req.getContrasenia()));
    u.setNombre(req.getNombre());
    u.setApellido(req.getApellido());

    Rol role = new Rol();
    role.setNombre(req.getRole().toUpperCase());
    u.setRoles(Set.of(role));

    usuarios.save(u);
  }

  public String login(LoginReq req) {
    String identificador = req.getUsernameOrEmail();
    String clave = req.getContrasenia();

    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(identificador, clave)
    );
    var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
    return jwt.generateToken(principal, Map.of());
  }
}
