package com.markys.markys.controller;

import com.markys.markys.model.Rol;
import com.markys.markys.model.Usuario;
import com.markys.markys.repository.RolRepository;
import com.markys.markys.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/repoadmin")
    public String mostrarRepoAdmin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        return "repoadmin";
    }

    @GetMapping("/admin/usuarios")
    public String mostrarUsuarios(@RequestParam(value = "rol", required = false) String rol, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);

        List<Usuario> usuarios;
        if (rol != null && !rol.equalsIgnoreCase("todos")) {
            usuarios = usuarioRepository.findByRolesNombre(rol);
        } else {
            usuarios = usuarioRepository.findAll();
            rol = "todos"; // Importante: por si viene null, para que se marque en el select
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("rolSeleccionado", rol); // 🔥 Aquí se pasa al HTML
        return "repoadmin";
    }



    @PostMapping("/admin/usuarios")
    public String crearUsuario(@RequestParam String username, @RequestParam String password, @RequestParam String nombre,
                               @RequestParam String apellido, @RequestParam String correo, @RequestParam String rol) {

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);

        Rol rolAsignado = rolRepository.findByNombre(rol).orElseGet(() -> {
            Rol r = new Rol();
            r.setNombre(rol);
            return rolRepository.save(r);
        });

        Set<Rol> roles = new HashSet<>();
        roles.add(rolAsignado);
        nuevoUsuario.setRoles(roles);

        usuarioRepository.save(nuevoUsuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            model.addAttribute("roles", rolRepository.findAll());
        }
        return "editarUsuario";
    }


    @PostMapping("/admin/usuarios/{id}/editar")
    public String actualizarUsuario(@PathVariable Long id, @RequestParam String username, @RequestParam String password,
                                    @RequestParam String nombre, @RequestParam String apellido, @RequestParam String correo,
                                    @RequestParam String rol) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setCorreo(correo);

            usuario.getRoles().clear();
            Rol rolAsignado = rolRepository.findByNombre(rol).orElseGet(() -> {
                Rol r = new Rol();
                r.setNombre(rol);
                return rolRepository.save(r);
            });
            usuario.getRoles().add(rolAsignado);

            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
        }
        return "confirmarEliminacion";
    }

    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String confirmarEliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/usuarios";
    }
}


