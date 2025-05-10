package com.markys.markys.service;

import com.markys.markys.model.Usuario;
import com.markys.markys.model.Rol;
import com.markys.markys.repository.UsuarioRepository;
import com.markys.markys.repository.RolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class RegistroService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registrarUsuario(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());

        if (existente.isPresent()) {
            return false; // ya existe
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Rol rolCliente = rolRepository.findByNombre("CLIENTE").orElseGet(() -> {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre("CLIENTE");
            return rolRepository.save(nuevoRol);
        });

        usuario.setRoles(Collections.singleton(rolCliente));

        usuarioRepository.save(usuario);
        return true;
    }
}
