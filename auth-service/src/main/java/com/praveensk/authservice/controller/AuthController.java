package com.praveensk.authservice.controller;

import com.praveensk.authservice.dto.LoginRequestDTO;
import com.praveensk.authservice.dto.LoginResponseDTO;
import com.praveensk.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        return tokenOptional.map(token -> ResponseEntity.ok(new LoginResponseDTO(token)))
                            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }


    @Operation(summary = "Validate JWT Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(authHeader.substring(7))
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
