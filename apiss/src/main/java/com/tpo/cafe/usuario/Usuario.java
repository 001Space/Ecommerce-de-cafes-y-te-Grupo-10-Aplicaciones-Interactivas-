package com.tpo.cafe.usuario;

import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor
public class Usuario {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String usuario;

  @Column(nullable = false, unique = true)
  private String correo;

  @Column(nullable = false)
  private String contrasenia;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private String apellido;

  @Column(nullable = false)
  private boolean habilitado = true;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "usuarios_roles",
    joinColumns = @JoinColumn(name = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Rol> roles;
}
