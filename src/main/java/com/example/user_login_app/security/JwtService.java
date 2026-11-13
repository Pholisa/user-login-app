package com.example.user_login_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms}") long expirationMs
  ){
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMs = expirationMs;
  }

  public String generateToken(String email){

    Date now = new Date();
    Date expiration = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
      .subject(email)
      .issuedAt(now)
      .expiration(expiration)
      .signWith(signingKey)
      .compact();
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
      .verifyWith(signingKey)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public boolean isTokenValid(String token, String email){
    try{
      Claims claims = extractClaims(token);

      return claims.getSubject().equals(email)
           && claims.getExpiration().after(new Date());
    }catch(Exception exception){
      return false;
    }
  }



}
