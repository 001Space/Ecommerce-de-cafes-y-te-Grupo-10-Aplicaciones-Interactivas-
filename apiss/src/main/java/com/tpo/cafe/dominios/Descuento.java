package com.tpo.cafe.dominios;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="descuentos")
@Getter @Setter @NoArgsConstructor
public class Descuento {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional=false)
  @JoinColumn(name="producto_id", unique = true)
  private Producto product;

  @Column(nullable=false)
  private Double porcentaje;

  @Column(name="inicia_en", nullable=false)
  private Instant startsAt;

  @Column(name="termina_en", nullable=false)
  private Instant endsAt;

  public boolean isActiveNow() {
    Instant now = Instant.now();
    return (now.isAfter(startsAt) || now.equals(startsAt)) && now.isBefore(endsAt);
  }
}
