package com.markys.markys.config;

// Importaciones de clases necesarias
import com.markys.markys.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration // Indica que esta clase es una clase de configuración de Spring
@EnableWebSecurity // Habilita la configuración de seguridad web
public class SecurityConfig {

    // Servicio para cargar detalles del usuario
    private final UserDetailsService userDetailsService;
    // Manejador personalizado para éxito de autenticación
    private final CustomAuthenticationSuccessHandler successHandler;
    // Filtro para JWT
    private final JwtRequestFilter jwtRequestFilter;

    // Constructor con inyección de dependencias
    public SecurityConfig(UserDetailsService userDetailsService,
                          CustomAuthenticationSuccessHandler successHandler,
                          JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (Cross-Site Request Forgery) - común en APIs REST
                .csrf(csrf -> csrf.disable())
                // Configuración de autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas que no requieren autenticación
                        .requestMatchers(
                                "/", "/carta", "/registro", "/contacto",
                                "/login", "/loginadmin", "/registroadmin", "/platillos/**",
                                "/inicio", "/css/*", "/img/*",
                                "/api/auth/login" // Endpoint para login con JWT
                        ).permitAll()
                        // Rutas que requieren rol ADMIN
                        .requestMatchers("/repoadmin", "/admin/**").hasRole("ADMIN")
                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )
                // Configuración del formulario de login
                .formLogin(login -> login
                        .loginPage("/login") // Página de login personalizada
                        .loginProcessingUrl("/login") // URL para procesar el login
                        .successHandler(successHandler) // Manejador de éxito personalizado
                        .permitAll() // Permitir acceso a todos
                )
                // Configuración de logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Redirigir a la página principal después de logout
                        .permitAll() // Permitir acceso a todos
                )
                // Configuración de manejo de sesiones (sin estado para APIs REST)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
                // Manejo de errores para APIs (devuelve JSON en vez de redirección)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"No autorizado\"}");
                        })
                );

        // Agregar el filtro JWT antes del filtro de autenticación tradicional
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración del proveedor de autenticación
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Establece el servicio de usuarios
        provider.setPasswordEncoder(passwordEncoder()); // Establece el codificador de contraseñas
        return provider;
    }

    // Configuración del administrador de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuración del codificador de contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}