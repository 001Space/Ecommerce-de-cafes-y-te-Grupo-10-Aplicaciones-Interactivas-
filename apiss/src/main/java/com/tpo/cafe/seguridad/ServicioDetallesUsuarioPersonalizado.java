package com.tpo.cafe.seguridad;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tpo.cafe.usuario.UsuarioRepositorio;

@Service
public class ServicioDetallesUsuarioPersonalizado implements UserDetailsService {

  private final UsuarioRepositorio repo;

  public ServicioDetallesUsuarioPersonalizado(UsuarioRepositorio repo) {
    this.repo = repo;
  }

  @Override
  public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
    var u = repo.findByUsuarioOrCorreo(usuario, usuario)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Set<SimpleGrantedAuthority> auths = u.getRoles().stream()
        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getNombre()))
        .collect(Collectors.toSet());

    return new User(u.getUsuario(), u.getContrasenia(), u.isHabilitado(), true, true, true, auths);
  }
}
