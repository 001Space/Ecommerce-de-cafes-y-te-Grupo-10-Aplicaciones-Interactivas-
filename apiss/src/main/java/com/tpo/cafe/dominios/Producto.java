package com.tpo.cafe.dominios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Getter @Setter @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Producto {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vendedor_id", nullable = false)
  @JsonIgnoreProperties({"contrasenia","roles"})
  private com.tpo.cafe.usuario.Usuario seller;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria category;

  @Column(nullable = false)
  private String nombre;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @Column(nullable = false)
  private Double precio;

  @Column(nullable = false)
  private Integer stock;

  @Column(nullable = false)
  private Boolean activo = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("producto-imagenes")
  private List<ImagenProducto> images = new ArrayList<>();

  @PrePersist
  public void prePersist() {
    if (createdAt == null) createdAt = Instant.now();
    if (activo == null) activo = true;
    if (stock == null) stock = 0;
  }
}
