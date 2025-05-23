package com.markys.markys.controller;

import com.markys.markys.model.Estado;
import com.markys.markys.model.Platillo;
import com.markys.markys.model.Rol;
import com.markys.markys.model.Usuario;
import com.markys.markys.repository.PlatilloRepository;
import com.markys.markys.repository.RolRepository;
import com.markys.markys.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.math.BigDecimal;

@Controller
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlatilloRepository platilloRepository;

    // Vista general del admin
    @GetMapping("/repoadmin")
    public String mostrarRepoAdmin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        return "repoadmin";
    }

    // Mostrar panel usuarios o platillos
    @GetMapping("/admin/usuarios")
    public String mostrarPanelUsuariosOPlatillos(
            @RequestParam(value = "rol", required = false) String rol,
            @RequestParam(value = "seccion", defaultValue = "usuarios") String seccion,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        model.addAttribute("seccion", seccion);

        if ("platillos".equalsIgnoreCase(seccion)) {
            List<Platillo> platillos = platilloRepository.findAll();
            model.addAttribute("platillos", platillos);
        } else {
            List<Usuario> usuarios;
            if (rol != null && !rol.equalsIgnoreCase("todos")) {
                usuarios = usuarioRepository.findByRolesNombre(rol);
            } else {
                usuarios = usuarioRepository.findAll();
                rol = "todos";
            }
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("rolSeleccionado", rol);
        }

        return "repoadmin";
    }

    // Crear nuevo usuario
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
        return "redirect:/admin/usuarios?seccion=usuarios";
    }

    // Obtener usuario por ID (usado en modales)
    @GetMapping("/admin/usuarios/{id}")
    @ResponseBody
    public Usuario obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Actualizar usuario desde modal
    @PostMapping("/admin/usuarios/actualizar")
    public String actualizarUsuarioDesdeModal(@RequestParam Long id, @RequestParam String username,
                                              @RequestParam(required = false) String password,
                                              @RequestParam String nombre, @RequestParam String apellido,
                                              @RequestParam String correo, @RequestParam String rol) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setUsername(username);
            if (password != null && !password.isBlank()) {
                usuario.setPassword(passwordEncoder.encode(password));
            }
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
        return "redirect:/admin/usuarios?seccion=usuarios";
    }

    // Eliminar usuario desde modal
    @PostMapping("/admin/usuarios/eliminar")
    public String eliminarUsuarioDesdeModal(@RequestParam Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/usuarios?seccion=usuarios";
    }

    // === CREAR PLATILLO CON IMAGEN ===
    @PostMapping("/admin/platillos/crear")
    public String crearPlatillo(@RequestParam("nombre") String nombre,
                                @RequestParam("descripcion") String descripcion,
                                @RequestParam("precio") Double precio,
                                @RequestParam("estado") String estado,
                                @RequestParam("imagen") MultipartFile imagenFile) {

        Platillo platillo = new Platillo();
        platillo.setNombre(nombre);
        platillo.setDescripcion(descripcion);
        platillo.setPrecio(BigDecimal.valueOf(precio));
        platillo.setEstado(estado.equalsIgnoreCase("DISPONIBLE") ? Estado.DISPONIBLE : Estado.AGOTADO);

        if (!imagenFile.isEmpty()) {
            try {
                String carpetaDestino = "src/main/resources/static/platimg/";
                String nombreArchivo = System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
                Path rutaArchivo = Paths.get(carpetaDestino + nombreArchivo);
                Files.createDirectories(rutaArchivo.getParent());
                imagenFile.transferTo(rutaArchivo);
                // Guardar ruta relativa para acceder desde HTML
                platillo.setImagen("/platimg/" + nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/admin/usuarios?seccion=platillos&error=imagen";
            }
        }

        platilloRepository.save(platillo);
        return "redirect:/admin/usuarios?seccion=platillos";
    }
}