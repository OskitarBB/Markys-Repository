package com.markys.markys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.markys.markys.model.Usuario;
import com.markys.markys.model.Rol;
import com.markys.markys.repository.UsuarioRepository;
import com.markys.markys.repository.RolRepository;

@Controller
public class RegistroAdminController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroAdminController(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registroadmin")
    public String registrarAdmin(@RequestParam String username, @RequestParam String password) {

        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "redirect:/loginadmin?error=usuarioExiste";
        }

        Usuario admin = new Usuario();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));

        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre("ADMIN");
                    return rolRepository.save(nuevoRol);
                });

        admin.getRoles().add(rolAdmin);
        usuarioRepository.save(admin);

        return "redirect:/repoadmin";
    }
}
