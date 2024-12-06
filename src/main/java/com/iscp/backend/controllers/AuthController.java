package com.iscp.backend.controllers;

import com.iscp.backend.dto.JwtRequest;
import com.iscp.backend.dto.JwtResponse;
import com.iscp.backend.exceptions.BadCredentialsException;
import com.iscp.backend.exceptions.InvalidCaptchaException;
import com.iscp.backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = AuthController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class AuthController {

    public static final String PATH ="/api/auth" ;

    private final AuthService authService;

    //LOGIN AUTHENTICATION FUNCTIONALITY
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) throws BadCredentialsException, InvalidCaptchaException {
        log.info("Received request to authentication user with username :{}",jwtRequest.getUsername());

        JwtResponse response=authService.generateAndAuthenticateToken(jwtRequest);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //LOGOUT FUNCTIONALITY
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Logout", description = "Logout user and blacklist JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    ResponseEntity<?> logout(@RequestHeader("Authorization") String token){
        log.info("Received request to logout user");
        Boolean response=authService.blacklistToken(token);
        Map<String, String> responseBody = new HashMap<>();
        if (response)
        {
            responseBody.put("message", "Logout successful");
            return  ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        else
        {
            responseBody.put("message", "Invalid token");
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }
}
