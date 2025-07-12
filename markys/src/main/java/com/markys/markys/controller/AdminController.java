package com.markys.markys.controller;

import com.markys.markys.model.Estado;
import com.markys.markys.model.Platillo;
import com.markys.markys.model.Rol;
import com.markys.markys.model.Usuario;
import com.markys.markys.repository.DetallePedidoRepository;
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
import com.markys.markys.model.EstadoUsuario;

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

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

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
    public String crearUsuario(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String nombre,
                               @RequestParam String apellido,
                               @RequestParam String correo,
                               @RequestParam String rol) {

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);

        // Asignar estado por defecto
        nuevoUsuario.setEstado(EstadoUsuario.vigente); // O puedes usar una cadena si usas String en lugar de enum
        nuevoUsuario.setComentario(""); // Comentario vacío por defecto

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

    // Vista de edición de usuario (opcional, si no usas modal)
    @GetMapping("/admin/usuarios/{id}/editar")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            model.addAttribute("roles", rolRepository.findAll());
            return "editarUsuario";
        } else {
            return "redirect:/admin/usuarios?seccion=usuarios&error=notfound";
        }
    }

    // Actualizar usuario desde modal
    @PostMapping("/admin/usuarios/{id}/editar")
    public String actualizarUsuarioDesdeFormulario(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String rol) {

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

    // Mostrar formulario de confirmación
    @GetMapping("/admin/usuarios/{id}/eliminar")
    public String confirmarEliminacion(@PathVariable Long id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            return "confirmarEliminacion";
        } else {
            return "redirect:/admin/usuarios?seccion=usuarios&error=notfound";
        }
    }

    // Procesar el retiro (cambio de estado y comentario)
    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(
            @PathVariable Long id,
            @RequestParam String comentario) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEstado(EstadoUsuario.retirado);
            usuario.setComentario(comentario);
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/usuarios?seccion=usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/eliminar-definitivo")
    public String eliminarDefinitivamente(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getEstado() == EstadoUsuario.retirado) {
                usuarioRepository.deleteById(id);
            }
        }
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

            // Validar y guardar imagen
            if (!imagenFile.isEmpty()) {
                try {
                    String carpetaDestino = "src/main/resources/static/img/";

                    // Limpiar el nombre del archivo (quitar espacios o caracteres raros)
                    String nombreOriginal = imagenFile.getOriginalFilename();
                    String nombreArchivo = nombreOriginal.replaceAll("\\s+", "_");

                    Path rutaArchivo = Paths.get(carpetaDestino + nombreArchivo);
                    Files.createDirectories(rutaArchivo.getParent());
                    imagenFile.transferTo(rutaArchivo);

                    // Guardar solo el nombre del archivo en la base de datos
                    platillo.setImagen(nombreArchivo);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "redirect:/admin/usuarios?seccion=platillos&error=imagen";
                }
            }

            platilloRepository.save(platillo);
            return "redirect:/admin/usuarios?seccion=platillos";
        }


    // === EDITAR PLATILLO CON POSIBLE CAMBIO DE IMAGEN ===
        @PostMapping("/admin/platillos/editar")
        public String editarPlatillo(@RequestParam("id") Long id,
                                     @RequestParam("nombre") String nombre,
                                     @RequestParam("descripcion") String descripcion,
                                     @RequestParam("precio") Double precio,
                                     @RequestParam("estado") String estado,
                                     @RequestParam("imagen") MultipartFile imagenFile) {

            Optional<Platillo> optionalPlatillo = platilloRepository.findById(id);
            if (optionalPlatillo.isPresent()) {
                Platillo platillo = optionalPlatillo.get();
                platillo.setNombre(nombre);
                platillo.setDescripcion(descripcion);
                platillo.setPrecio(BigDecimal.valueOf(precio));
                platillo.setEstado(estado.equalsIgnoreCase("DISPONIBLE") ? Estado.DISPONIBLE : Estado.AGOTADO);

                if (imagenFile != null && !imagenFile.isEmpty()) {
                    try {
                        String carpetaDestino = "src/main/resources/static/img/";
                        String nombreArchivo = imagenFile.getOriginalFilename();
                        Path rutaArchivo = Paths.get(carpetaDestino + nombreArchivo);
                        Files.createDirectories(rutaArchivo.getParent());
                        imagenFile.transferTo(rutaArchivo);
                        platillo.setImagen(nombreArchivo);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "redirect:/admin/usuarios?seccion=platillos&error=imagen";
                    }
                }

                platilloRepository.save(platillo);
            }

            return "redirect:/admin/usuarios?seccion=platillos";
        }

        // === ELIMINAR PLATILLO POR ID ===
        @PostMapping("/admin/platillos/{id}/eliminar")
        public String eliminarPlatillo(@PathVariable Long id) {
            platilloRepository.deleteById(id);
            return "redirect:/admin/usuarios?seccion=platillos";
        }
    @GetMapping("/admin/platillos/nuevo")
    public String mostrarFormularioNuevoPlatillo() {
        return "nuevoPlatillo";
    }

    @GetMapping("/admin/platillos/{id}/editar")
    public String mostrarFormularioEditarPlatillo(@PathVariable Long id, Model model) {
        Optional<Platillo> optional = platilloRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("platillo", optional.get());
            return "editarPlatillo";
        }
        return "redirect:/admin/usuarios?seccion=platillos";
    }
    @GetMapping("/admin/ventas")
    public String mostrarVentas(Model model) {
        List<Object[]> datosPlatillos = detallePedidoRepository.contarPedidosPorPlatillo();
        model.addAttribute("datosPlatillos", datosPlatillos);
        model.addAttribute("seccion", "ventas");
        return "repoadmin"; // tu mismo archivo HTML
    }


}




