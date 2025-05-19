package com.markys.markys.controller;

import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PlatilloVistaController {

    @Autowired
    private PlatilloRepository platilloRepository;

    @GetMapping("/platillos")
    public String mostrarPlatillos(Model model) {
        List<Platillo> platillos = platilloRepository.findAll();
        model.addAttribute("platillos", platillos);
        return "carta";
    }

    @GetMapping("/platillos/buscar")
    public String buscarPlatillos(@RequestParam("nombre") String nombre, Model model) {
        List<Platillo> resultados = platilloRepository.buscarPorNombre(nombre);
        model.addAttribute("platillos", resultados);
        return "carta";
    }
}
