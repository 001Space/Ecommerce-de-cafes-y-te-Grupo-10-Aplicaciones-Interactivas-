package com.tpo.cafe.dominios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items_pedido")
@Getter @Setter @NoArgsConstructor
public class ItemPedido {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "pedido_id")
  private Pedido pedido;

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
