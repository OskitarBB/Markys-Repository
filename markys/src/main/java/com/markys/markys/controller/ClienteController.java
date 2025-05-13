package com.markys.markys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @GetMapping("/inicio")
    public String mostrarInicioCliente() {
        return "inicio";
    }
}
