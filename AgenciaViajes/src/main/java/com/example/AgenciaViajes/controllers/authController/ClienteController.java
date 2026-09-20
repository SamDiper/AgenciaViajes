package com.example.AgenciaViajes.controllers.authController;



import com.example.AgenciaViajes.dto.RegistroClienteDTO;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.servicios.ClienteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Obtener cliente asociado al ID del Usuario

    // 1. LISTAR TODOS LOS CLIENTES
    @GetMapping("/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("titulo", "Lista de Clientes");
        return "auto/cliente/Lista"; // Nombre de tu vista HTML
    }

    // 2. FORMULARIO DE REGISTRO
    @GetMapping
    public String nuevoForm(Model model) {
        model.addAttribute("registroClienteDTO", new RegistroClienteDTO());
        model.addAttribute("titulo", "Registrar Cliente");
        return "auth/cliente/register";
    }

    // 3. PROCESAR REGISTRO (POST)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("registroClienteDTO") RegistroClienteDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // 1. Si hay errores de validación en el formulario DTO
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registrar Cliente");
            return "auth/cliente/register"; // Permanece en el formulario para mostrar los mensajes de error
        }

        try {
            clienteService.registrarCliente(dto);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente registrado exitosamente");

            // 2. Usar 'redirect:' para navegar al login tras guardar
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/cliente/register";
        }
    }

    // 4. FORMULARIO DE EDICIÓN
    @GetMapping("/perfil")
    public String verPerfil(Authentication authentication, Model model) {
        // 1. Obtenemos el correo del usuario logueado
        String correo = authentication.getName();
        Cliente cliente = clienteService.obtenerPorCorreo(correo);

        // 2. Preparamos el DTO con los datos actuales para el formulario
        RegistroClienteDTO dto = new RegistroClienteDTO();
        dto.setNombreCompleto(cliente.getNombreCompleto());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCorreo(cliente.getCorreo());

        // 3. Pasamos AMBAS variables al modelo
        model.addAttribute("cliente", cliente);
        model.addAttribute("registroClienteDTO", dto); // <--- ESTO ES LO QUE FALTABA
        model.addAttribute("titulo", "Mi Perfil y Reservas");

        model.addAttribute("reservasActivas", Collections.emptyList());
        model.addAttribute("historialViajes", Collections.emptyList());

        return "auth/cliente/Formulario";
    }

    // 5. ACTUALIZAR (Procesado mediante POST desde el formulario)
    // 2. ACTUALIZAR PERFIL (/clientes/actualizar)
    @PostMapping("/actualizar")
    public String actualizar(Authentication authentication,
                             @ModelAttribute("registroClienteDTO") RegistroClienteDTO dto,
                             RedirectAttributes redirectAttributes) {

        // Obtenemos el cliente logueado por su correo
        String correo = authentication.getName();
        Cliente clienteActual = clienteService.obtenerPorCorreo(correo);

        // Actualizamos los datos usando el ID interno del cliente
        clienteService.actualizarPerfil(clienteActual.getIdCliente(), dto);

        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");

        return "redirect:/clientes/perfil";
    }

}
