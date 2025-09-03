package com.tpo.cafe.controladores;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManejadorExcepcionesApi {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleRuntime(RuntimeException ex) {
    return ResponseEntity.badRequest().body(Map.of(
      "timestamp", Instant.now().toString(),
      "error", ex.getMessage()
    ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
      "timestamp", Instant.now().toString(),
      "error", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()
    ));
  }
}
