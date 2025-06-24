package com.markys.markys.controller;

import com.markys.markys.model.Usuario;
import com.markys.markys.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    // Expresión regular para validar contraseña segura:
    // - Mínimo 8 caracteres
    // - Al menos una mayúscula
    // - Al menos un número
    // - Al menos un carácter especial
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    @GetMapping("/editar")
    public String mostrarFormularioEdicion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Usuario> usuarioOptional = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuarioOptional.isPresent()) {
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
            Usuario usuarioExistente = usuarioService.obtenerUsuarioPorId(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar campos básicos
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setCorreo(usuario.getCorreo());
            usuarioExistente.setUsername(usuario.getUsername());

            boolean passwordChanged = false;

            // Validar y actualizar contraseña
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                    return "redirect:/perfil/editar";
                }

                if (!newPassword.matches(PASSWORD_PATTERN)) {
                    redirectAttributes.addFlashAttribute("error",
                            "La contraseña debe tener: mínimo 8 caracteres, al menos una mayúscula, un número y un carácter especial (@#$%^&+=!)");
                    return "redirect:/perfil/editar";
                }

                usuarioExistente.setPassword(passwordEncoder.encode(newPassword));
                passwordChanged = true;
            }

            usuarioService.actualizarUsuario(usuarioExistente.getId(), usuarioExistente);

            if (passwordChanged) {
                // Cerrar sesión si se cambió la contraseña
                SecurityContextHolder.clearContext();
                redirectAttributes.addFlashAttribute("success",
                        "Perfil actualizado correctamente. Por seguridad, debe iniciar sesión nuevamente.");
                return "redirect:/login";
            } else {
                // Actualizar el contexto de seguridad si solo se cambiaron otros campos
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        usuarioExistente.getUsername(),
                        auth.getCredentials(),
                        auth.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);

                redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
                return "redirect:/perfil/editar";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }
}