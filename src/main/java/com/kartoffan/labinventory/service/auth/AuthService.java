package com.kartoffan.labinventory.service.auth;

import org.springframework.http.ResponseCookie;

import com.kartoffan.labinventory.dto.auth.AuthResponse;
import com.kartoffan.labinventory.dto.auth.LoginRequest;
import com.kartoffan.labinventory.dto.auth.SignupRequest;

public interface AuthService {

  AuthResult login(LoginRequest request);

  AuthResult signup(SignupRequest request);

  record AuthResult(ResponseCookie cookie, AuthResponse response) {}

}
