package com.tpo.cafe.dto;

import lombok.Data;

@Data
public class DtosCarrito {

  @Data
  public static class AddItemReq {
    private Long productId;
    private Integer cantidad;
  }

  @Data
  public static class UpdateItemReq {
    private Integer cantidad;
  }
}
