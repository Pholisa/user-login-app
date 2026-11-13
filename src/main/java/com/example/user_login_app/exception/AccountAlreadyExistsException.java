package com.example.user_login_app.exception;

public class AccountAlreadyExistsException extends RuntimeException{

  public AccountAlreadyExistsException(String message){
    super(message);
  }
}
