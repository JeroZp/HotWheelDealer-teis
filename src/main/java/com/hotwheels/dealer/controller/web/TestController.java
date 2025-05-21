package com.hotwheels.dealer.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("titulo", "Página de Prueba");
        model.addAttribute("mensaje", "¡La aplicación funciona correctamente!");
        return "web/test";
    }
}