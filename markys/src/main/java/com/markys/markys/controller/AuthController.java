package com.markys.markys.controller;

// Importaciones necesarias
import com.markys.markys.security.JwtUtil; // Utilidad para manejar JWT
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/auth") // Ruta base para todos los endpoints de este controlador
public class AuthController {

    // Inyección de dependencias
    private final AuthenticationManager authenticationManager; // Para autenticar usuarios
    private final JwtUtil jwtUtil; // Para generar y manejar tokens JWT

    // Constructor con inyección de dependencias
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint para login que acepta peticiones POST en /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        try {
            // Autenticar al usuario con las credenciales proporcionadas
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.get("username"), // Obtiene el nombre de usuario del Map
                            user.get("password") // Obtiene la contraseña del Map
                    )
            );

            // Si la autenticación es exitosa, genera un token JWT
            String jwt = jwtUtil.generateToken(user.get("username"));

            // Crea la respuesta con el token
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);

            // Devuelve la respuesta con estado HTTP 200 (OK)
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // Si falla la autenticación, crea un mensaje de error
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");

            // Devuelve la respuesta con estado HTTP 401 (Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}