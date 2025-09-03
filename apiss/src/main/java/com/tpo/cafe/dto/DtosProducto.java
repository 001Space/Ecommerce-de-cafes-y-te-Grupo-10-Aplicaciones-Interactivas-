package com.tpo.cafe.dto;

import java.util.List;
import lombok.Data;

@Data
public class DtosProducto {

  @Data
  public static class CreateReq {
    private Long categoryId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Boolean activo;
    private List<String> imageUrls;
  }

  @Data
  public static class UpdateReq {
    private Long categoryId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Boolean activo;
    private List<String> imageUrls;
  }
}
