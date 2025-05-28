package com.markys.markys.security;

// Importaciones para manejo de JWT
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.stereotype.Component; // Para que Spring lo gestione como componente

@Component // Indica que esta clase es un componente gestionado por Spring
public class JwtUtil {

    // Clave secreta para firmar los tokens - ¡En producción debe ser más segura y no estar hardcodeada!
    private static final String SECRET_KEY = "MiClaveSecretaSuperSegura1234567890!!!";

    // Tiempo de expiración del token (1 hora en milisegundos)
    private static final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hora

    // Metodo para obtener la clave de firma
    private SecretKey getKey() {
        // Convierte la cadena SECRET_KEY en una clave HMAC-SHA
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Genera un nuevo token JWT
     * @param username Nombre de usuario a incluir en el token
     * @return Token JWT firmado
     */
    public String generateToken(String username) {
        return Jwts.builder() // Constructor de tokens
                .setSubject(username) // Establece el sujeto (usuario)
                .setIssuedAt(new Date()) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Fecha de expiración
                .signWith(getKey(), SignatureAlgorithm.HS256) // Firma con algoritmo HS256
                .compact(); // Convierte a formato string
    }

    /**
     * Extrae el nombre de usuario de un token JWT
     * @param token Token JWT
     * @return Nombre de usuario contenido en el token
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder() // Constructor de parser
                .setSigningKey(getKey()) // Establece la clave para verificar firma
                .build()
                .parseClaimsJws(token) // Parsea y valida el token
                .getBody() // Obtiene el cuerpo (claims)
                .getSubject(); // Extrae el sujeto (username)
    }

    /**
     * Verifica si un token JWT es vlido
     * @param token Token a validar
     * @return true si el token es válido, false si es inválido o ha expirado
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token); // Intenta parsear el token
            return true; // Si no lanza excepción, el token es válido
        } catch (JwtException | IllegalArgumentException e) {
            // Captura excepciones de token inválido, expirado o mal formado
            return false;
        }
    }
}