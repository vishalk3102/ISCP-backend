package com.iscp.backend.services;

import com.iscp.backend.dto.JwtRequest;
import com.iscp.backend.dto.JwtResponse;
import com.iscp.backend.exceptions.BadCredentialsException;

public interface AuthService {
    JwtResponse generateAndAuthenticateToken(JwtRequest jwtRequest) throws BadCredentialsException;

    Boolean blacklistToken(String token);

    boolean validateCaptcha(String captchaToken);
}
