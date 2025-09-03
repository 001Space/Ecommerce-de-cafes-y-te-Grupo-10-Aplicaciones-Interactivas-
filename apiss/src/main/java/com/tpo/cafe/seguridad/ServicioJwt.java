package com.tpo.cafe.seguridad;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class ServicioJwt {

  @Value("${app.jwt.secret:ZmFrZV9zZWNyZXRfMTIzNDU2Nzg5MA==}")
  private String secret;

  @Value("${app.jwt.expiration-ms:86400000}") // 1 día
  private long expirationMs;

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    return resolver.apply(claims);
  }

  public String generateToken(UserDetails user, Map<String, Object> extra) {
    return Jwts.builder()
        .setClaims(extra)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails user) {
    String usuario = extractUsername(token);
    return usuario.equals(user.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    Date exp = extractClaim(token, Claims::getExpiration);
    return exp.before(new Date());
  }
}
