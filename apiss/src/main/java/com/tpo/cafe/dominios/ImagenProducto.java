package com.tpo.cafe.dominios;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagenes_producto")
@Getter @Setter @NoArgsConstructor
public class ImagenProducto {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String url;

  @Column(name = "es_principal", nullable = false)
  private boolean esPrincipal = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "producto_id", nullable = false)
  @JsonBackReference("producto-imagenes")
  private Producto product;
}
