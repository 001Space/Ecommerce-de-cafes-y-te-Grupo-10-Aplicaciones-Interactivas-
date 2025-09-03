package com.tpo.cafe.dominios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items_carrito")
@Getter @Setter @NoArgsConstructor
public class ItemCarrito {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "carrito_id")
  private Carrito carrito;

  @ManyToOne(optional = false)
  @JoinColumn(name = "producto_id")
  private Producto producto;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(name = "precio_unitario", nullable = false)
  private Double precioUnitario;

  @Column(name = "porcentaje_descuento", nullable = false)
  private Double porcentajeDescuento = 0.0;
}
