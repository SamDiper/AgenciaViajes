package com.example.AgenciaViajes.controllers.authController;

import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import com.example.AgenciaViajes.repositorio.DestinoRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    private final ClienteRepository clienteRepository;
    private final DestinoRepository destinoRepository;

    public HomeController(ClienteRepository clienteRepository, DestinoRepository destinoRepository) {
        this.clienteRepository = clienteRepository;
        this.destinoRepository = destinoRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Destinos: siempre se muestran, esté logueado o no
        model.addAttribute("destinos", destinoRepository.findTop6ByOrderByIdDestinoDesc());

        // Datos del cliente: solo si hay sesión activa
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(auth.getName());
            clienteOpt.ifPresent(cliente -> {
                model.addAttribute("cliente", cliente);
                model.addAttribute("nombreCliente", cliente.getNombreCompleto());
                model.addAttribute("correoUsuario", cliente.getCorreo());
                model.addAttribute("idCliente", cliente.getIdCliente());
            });
        }

        return "auth/Home/index"; 
    }
}