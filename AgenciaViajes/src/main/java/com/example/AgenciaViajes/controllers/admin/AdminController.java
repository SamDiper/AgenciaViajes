package com.example.AgenciaViajes.controllers.admin;

import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ClienteRepository clienteRepository;

    public AdminController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(auth.getName());
            clienteOpt.ifPresent(cliente -> {
                model.addAttribute("cliente", cliente);
                model.addAttribute("nombreAdmin", cliente.getNombreCompleto());
                model.addAttribute("correoAdmin", cliente.getCorreo());
            });
        }
        model.addAttribute("activeSection", "dashboard");
        return "admin/index";
    }
}
