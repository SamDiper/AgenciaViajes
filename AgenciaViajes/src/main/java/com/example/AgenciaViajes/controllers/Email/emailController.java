package com.example.AgenciaViajes.controllers.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.AgenciaViajes.servicios.Email.EmailServicio;

import java.util.HashMap;
import java.util.Map;

@Controller
public class emailController {

    @Autowired
    private EmailServicio emailServ;

    @PostMapping("/procesarReserva")
    public String procesarReserva(@RequestParam String correoCliente,
                                  @RequestParam String nombreCliente,
                                  @RequestParam String destinoViaje,
                                  @RequestParam String fechaViaje,
                                  Model model) {
        try {
            Map<String, Object> variablesReserva = new HashMap<>(); 
            variablesReserva.put("nombre", nombreCliente);
            variablesReserva.put("destino", destinoViaje);
            variablesReserva.put("fecha", fechaViaje);
            variablesReserva.put("mensaje", "¡Tu reserva ha sido confirmada exitosamente!");

            // generación de pdf - falta implementar
            byte[] documentoAdjunto = null;
            String nombreAdjunto = "itinerario_" + destinoViaje.replaceAll("\\s+", "") + ".pdf";

            String asunto = "Confirmación de Reserva - " + destinoViaje;
            
            emailServ.enviarCorreoReserva(
                    correoCliente, 
                    asunto, 
                    "Email/correo", 
                    variablesReserva, 
                    documentoAdjunto, 
                    nombreAdjunto
            );

            model.addAttribute("resultado", "Reserva completada. Revisa tu correo en " + correoCliente); 
            return "reservaExitosa";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("resultado", "Error al procesar la reserva: " + e.getMessage()); 
            return "formularioReserva"; 
        }
    }
}
