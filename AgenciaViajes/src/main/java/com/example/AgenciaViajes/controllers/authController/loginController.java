package com.example.AgenciaViajes.controllers.authController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class loginController {


    @GetMapping
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        // Si Spring Security detecta un error en las credenciales
        if (error != null) {
            model.addAttribute("errorMessage", "Correo o contraseña incorrectos.");
        }

        // Si el usuario cerró sesión exitosamente
        if (logout != null) {
            model.addAttribute("logoutMessage", "Has cerrado sesión correctamente.");
        }

        return "auth/login"; // Ajusta la ruta a la vista de tu plantilla login.html
    }
}
