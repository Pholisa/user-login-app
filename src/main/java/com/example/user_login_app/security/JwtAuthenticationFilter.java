package com.example.user_login_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService){
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException{

    String authHeader = request.getHeader("Authorization");

    if(authHeader == null || !authHeader.startsWith("Bearer")){
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);

    try{
      String email = jwtService.extractClaims(token).getSubject();

      if(email != null
              && SecurityContextHolder.getContext().getAuthentication() == null
              && jwtService.isTokenValid(token, email)){

        UsernamePasswordAuthenticationToken authenticationFilter =
          new UsernamePasswordAuthenticationToken(email, null, java.util.Collections.emptyList());

        SecurityContextHolder.getContext()
          .setAuthentication(authenticationFilter);
      }
    } catch (Exception exception){
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }
}
