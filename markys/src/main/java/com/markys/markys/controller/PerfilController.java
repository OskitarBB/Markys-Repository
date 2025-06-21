package com.markys.markys.controller;

import com.markys.markys.model.Usuario;
import com.markys.markys.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/editar")
    public String mostrarFormularioEdicion(Model model) {
        // Obtener el usuario actual autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuarioOptional = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOptional.isPresent()) {
            // No enviar la contraseña al formulario por seguridad
            Usuario usuario = usuarioOptional.get();
            usuario.setPassword(""); // Limpiar la contraseña para el formulario
            model.addAttribute("usuario", usuario);
        } else {
            return "redirect:/login?error=user_not_found";
        }

        return "edit_profile";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener el usuario actual de la base de datos
            Usuario usuarioExistente = usuarioService.obtenerUsuarioPorId(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar campos básicos (excepto roles)
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setCorreo(usuario.getCorreo());
            usuarioExistente.setUsername(usuario.getUsername());

            // Actualizar contraseña solo si se proporcionó una nueva
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                    return "redirect:/perfil/editar";
                }
                usuarioExistente.setPassword(passwordEncoder.encode(newPassword));
            }

            // Guardar los cambios
            usuarioService.actualizarUsuario(usuarioExistente.getId(), usuarioExistente);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/perfil/editar";
    }
}