package com.example.AgenciaViajes.controllers.authController;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        // Obtenemos el correo del usuario autenticado
        if (authentication != null) {
            model.addAttribute("usuarioCorreo", authentication.getName());
        }
        return "/Home/index"; // Mapea a src/main/resources/templates/home.html
    }
}
