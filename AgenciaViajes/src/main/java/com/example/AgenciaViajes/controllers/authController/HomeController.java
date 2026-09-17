package com.example.AgenciaViajes.controllers.authController;

import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {
    private final ClienteRepository clienteRepository;

    public HomeController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        if (authentication != null) {
            String correo = authentication.getName();
            Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(correo);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                model.addAttribute("cliente", cliente);
                model.addAttribute("nombreCliente", cliente.getNombreCompleto());
                model.addAttribute("correoUsuario", cliente.getCorreo());
                model.addAttribute("idCliente", cliente.getIdCliente());
            }
        }
        return "auth/Home/index";
    }
}
