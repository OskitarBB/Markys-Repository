package com.markys.markys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginAdminController {

    @GetMapping("/loginadmin")
    public String mostrarLoginAdmin() {
        return "loginadmin"; // busca loginadmin.html en templates
    }
}
