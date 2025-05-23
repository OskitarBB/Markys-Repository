package com.markys.markys.controller;

import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import com.markys.markys.model.Estado;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;

@Controller
@RequestMapping("/admin/platillos")
public class PlatilloController {

    @Autowired
    private PlatilloRepository platilloRepository;

    @PostMapping
    public String guardarPlatillo(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("estado") String estado,
            @RequestParam("imagen") MultipartFile imagenFile
    ) {
        try {
            String nombreArchivo = StringUtils.cleanPath(imagenFile.getOriginalFilename());
            String uploadDir = "src/main/resources/static/platimg/";
            Path rutaArchivo = Paths.get(uploadDir + nombreArchivo);
            Files.copy(imagenFile.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            Platillo platillo = new Platillo();
            platillo.setNombre(nombre);
            platillo.setDescripcion(descripcion);
            platillo.setPrecio(precio);
            platillo.setEstado(estado.equals("DISPONIBLE") ? Estado.DISPONIBLE : Estado.AGOTADO);
            platillo.setImagen(nombreArchivo);

            platilloRepository.save(platillo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/usuarios?seccion=platillos";
    }

    // NUEVO: Método GET para mostrar formulario edición
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Platillo platillo = platilloRepository.findById(id).orElse(null);
        if (platillo == null) {
            return "redirect:/admin/usuarios?seccion=platillos"; // O página de error personalizada
        }
        model.addAttribute("platillo", platillo);
        return "admin/editar-platillo"; // Nombre de la vista para editar
    }

    @PostMapping("/editar")
    public String editarPlatilloDesdeModal(
            @RequestParam("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("estado") String estado,
            @RequestParam(value = "imagen", required = false) MultipartFile imagenFile
    ) {
        Platillo platillo = platilloRepository.findById(id).orElse(null);
        if (platillo != null) {
            platillo.setNombre(nombre);
            platillo.setDescripcion(descripcion);
            platillo.setPrecio(precio);
            platillo.setEstado(estado.equals("DISPONIBLE") ? Estado.DISPONIBLE : Estado.AGOTADO);

            if (imagenFile != null && !imagenFile.isEmpty()) {
                try {
                    String nombreArchivo = StringUtils.cleanPath(imagenFile.getOriginalFilename());
                    String uploadDir = "src/main/resources/static/platimg/";
                    Path rutaArchivo = Paths.get(uploadDir + nombreArchivo);
                    Files.copy(imagenFile.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                    platillo.setImagen(nombreArchivo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            platilloRepository.save(platillo);
        }

        return "redirect:/admin/usuarios?seccion=platillos";
    }

    // NUEVO: Método POST para eliminar platillo
    @PostMapping("/{id}/eliminar")
    public String eliminarPlatillo(@PathVariable Long id) {
        platilloRepository.deleteById(id);
        return "redirect:/admin/usuarios?seccion=platillos";
    }
}
