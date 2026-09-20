package com.example.AgenciaViajes.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @GetMapping
    public String vistaReservas(Model model) {
        model.addAttribute("tituloPanel", "Gestión de Reservas");
        
        return "auth/reservas/reserva"; 
    }
}