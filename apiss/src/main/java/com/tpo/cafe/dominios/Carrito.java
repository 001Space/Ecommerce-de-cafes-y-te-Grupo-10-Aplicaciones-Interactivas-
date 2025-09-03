package com.tpo.cafe.dominios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Getter @Setter @NoArgsConstructor
public class Carrito {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id")
  private com.tpo.cafe.usuario.Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoCarrito estado = EstadoCarrito.OPEN;

  @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemCarrito> items = new ArrayList<>();
}
