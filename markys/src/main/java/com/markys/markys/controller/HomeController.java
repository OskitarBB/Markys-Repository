package com.markys.markys.controller;

import com.markys.markys.model.Platillo;
import com.markys.markys.model.Usuario;
import com.markys.markys.repository.RolRepository;
import com.markys.markys.repository.UsuarioRepository;
import com.markys.markys.service.RegistroService;
import com.markys.markys.service.ServicioPlatillo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ServicioPlatillo servicioPlatillo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistroService registroService;

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/carta")
    public String mostrarCarta(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        List<Platillo> platillos;

        if (nombre != null && !nombre.isEmpty()) {
            platillos = servicioPlatillo.buscarPorNombre(nombre);
        } else {
            platillos = servicioPlatillo.obtenerTodos();
        }

        model.addAttribute("platillos", platillos);
        return "carta";
    }


    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }


    @GetMapping("/delivery")
    public String mostrarDelivery(Model model) {
        List<Platillo> listaPlatillos = servicioPlatillo.obtenerTodos();
        model.addAttribute("platillos", listaPlatillos);
        return "delivery";
    }
    @GetMapping("/delivery/buscar")
    public String buscarPlatillosDelivery(@RequestParam("nombre") String nombre, Model model) {
        List<Platillo> platillosFiltrados = servicioPlatillo.buscarPorNombre(nombre);
        model.addAttribute("platillos", platillosFiltrados);
        return "delivery";
    }


    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model) {
        boolean registrado = registroService.registrarUsuario(usuario);

        if (!registrado) {
            model.addAttribute("error", "El nombre de usuario ya existe");
            return "registro";
        }

        return "redirect:/login";
    }

    @GetMapping("/edit_profile")
    public String mostrarEditarPerfil(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Pasar el usuario al modelo para prellenar el formulario
        model.addAttribute("usuario", usuario);
        return "edit_profile"; // Nombre de la plantilla Thymeleaf
    }



    //Rutas post-pago
    @GetMapping("/success")
    public String pagoExitoso() {
        return "success";
    }

    @GetMapping("/failure")
    public String pagoFallido() {
        return "failure";
    }

    @GetMapping("/pending")
    public String pagoPendiente() {
        return "pending";
    }
}
