package com.example.user_login_app.service;

import com.example.user_login_app.dto.AuthResponseDTO;
import com.example.user_login_app.dto.LoginRequestDTO;
import com.example.user_login_app.dto.RegisterRequestDTO;
import com.example.user_login_app.entity.User;
import com.example.user_login_app.exception.AccountAlreadyExistsException;
import com.example.user_login_app.exception.InvalidCredentialsException;
import com.example.user_login_app.repository.UserRepository;
import com.example.user_login_app.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    JwtService jwtService){
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public void register(RegisterRequestDTO requestDTO){
    String email = requestDTO.getEmail()
      .trim()
      .toLowerCase(Locale.ROOT);

    if(userRepository.existsByEmail(email)){
      throw new AccountAlreadyExistsException(
        "An account with this email already exists"
      );
    }

    User user = new User();
    user.setName(requestDTO.getName().trim());
    user.setEmail(email);

    String hashedPassword = passwordEncoder.encode(requestDTO.getPassword());

    user.setPassword(hashedPassword);
    userRepository.save(user);
  }

  public AuthResponseDTO login(LoginRequestDTO loginRequestDTO){
    String email = loginRequestDTO.getEmail()
        .trim()
        .toLowerCase(Locale.ROOT);

    Optional<User> optionalUser = userRepository.findByEmail(email);

    User user = optionalUser.orElseThrow(
      () -> new InvalidCredentialsException(
        "Invalid email or password"

      )
    );

    boolean passwordMatches = passwordEncoder.matches(
      loginRequestDTO.getPassword(), user.getPassword());

    if(!passwordMatches){
      throw new InvalidCredentialsException(
        "Invalid email or password"
      );
    }

    String token = jwtService.generateToken(user.getEmail());

    return new AuthResponseDTO("Login successful", token);
  }
}
