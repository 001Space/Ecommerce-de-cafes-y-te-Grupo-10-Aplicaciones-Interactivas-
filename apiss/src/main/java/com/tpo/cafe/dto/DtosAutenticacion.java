package com.tpo.cafe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtosAutenticacion {

  @Data
  public static class RegisterReq {
    @NotBlank private String usuario;
    @NotBlank @Email private String correo;
    @NotBlank private String contrasenia;
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotBlank private String role; 
  }

  @Data
  public static class LoginReq {
    @NotBlank private String usernameOrEmail; 
    @NotBlank private String contrasenia;
  }

  @Data
  public static class TokenRes {
    private final String token;
    private final String tokenType = "Bearer";
  }
}
