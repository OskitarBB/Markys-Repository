package com.markys.markys.security;

// Importaciones necesarias
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull; //
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Token de autenticación
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Indica que esta clase es un componente gestionado por Spring
public class JwtRequestFilter extends OncePerRequestFilter { // Extiende de filtro que se ejecuta una vez por petición

    // Dependencias inyectadas
    private final JwtUtil jwtUtil; // Utilidad para manejar JWT
    private final UserDetailsService userDetailsService; // Servicio para cargar detalles de usuario

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // La petición HTTP
            @NonNull HttpServletResponse response, // La respuesta HTTP
            @NonNull FilterChain filterChain) // Cadena de filtros a ejecutar
            throws ServletException, IOException {

        // 1. Extraer el header de autorización
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Verificar si el header existe y comienza con "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extraer el token (elimina "Bearer ")

            // 3. Validar el token JWT
            if (jwtUtil.isTokenValid(jwt)) {
                username = jwtUtil.extractUsername(jwt); // Extraer el nombre de usuario del token
            }
        }

        // 4. Si tenemos un usuario válido y no hay autenticación en el contexto actual
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargar los detalles del usuario desde la base de datos
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Validar nuevamente el token (doble verificación)
            if (jwtUtil.isTokenValid(jwt)) {
                // Crear un token de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // Principal (usuario)
                                null, // Credenciales (null porque JWT ya las validó)
                                userDetails.getAuthorities() // Roles/permisos del usuario
                        );

                // Establecer detalles de la autenticación (IP, sesión, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}