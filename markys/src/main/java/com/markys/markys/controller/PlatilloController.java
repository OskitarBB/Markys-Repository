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

}
