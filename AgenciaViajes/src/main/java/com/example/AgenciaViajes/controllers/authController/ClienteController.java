package com.example.AgenciaViajes.controllers.authController;

import com.example.AgenciaViajes.dto.RegistroClienteDTO;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.servicios.ClienteService;
import com.example.AgenciaViajes.servicios.ReservaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ReservaService reservaService;
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService, ReservaService reservaService) {
        this.clienteService = clienteService;
        this.reservaService = reservaService;
    }

    // 1. LISTAR TODOS LOS CLIENTES
    @GetMapping("/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("titulo", "Lista de Clientes");
        return "auto/cliente/Lista";
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

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registrar Cliente");
            return "auth/cliente/register";
        }

        try {
            clienteService.registrarCliente(dto);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente registrado exitosamente");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/cliente/register";
        }
    }

// 4. FORMULARIO DE EDICIÓN
@GetMapping("/editar/{id}")
public String editarForm(@PathVariable Integer id, Model model) {
    Cliente cliente = clienteService.obtenerTodos()
            .stream()
            .filter(c -> c.getIdCliente().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

    RegistroClienteDTO dto = new RegistroClienteDTO();
    dto.setNombreCompleto(cliente.getNombreCompleto());
    dto.setTipoDocumento(cliente.getTipoDocumento().name());
    dto.setDocumento(cliente.getDocumento());
    dto.setCorreo(cliente.getCorreo());
    dto.setTelefono(cliente.getTelefono());
    dto.setDireccion(cliente.getDireccion());

    // Traemos todas las reservas del cliente usando su nombreUsuario
    var todasLasReservas = reservaService.misReservas(cliente.getUsuario().getNombreUsuario());

    var reservasActivas = todasLasReservas.stream()
            .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA)
            .toList();

    var historialViajes = todasLasReservas.stream()
            .filter(r -> r.getEstadoReserva() == EstadoReserva.CANCELADA)
            .toList();

    model.addAttribute("cliente", cliente);
    model.addAttribute("registroClienteDTO", dto);
    model.addAttribute("titulo", "Editar Cliente");
    model.addAttribute("reservasActivas", reservasActivas);
    model.addAttribute("historialViajes", historialViajes);
    return "auth/cliente/Formulario";
}

    // 5. ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute("cliente") RegistroClienteDTO dto) {
        clienteService.actualizarPerfil(id, dto);
        return "redirect:/clientes";
    }

    @GetMapping("/mi-perfil")
public String miPerfil(Authentication auth) {
    Integer id = clienteService.obtenerPorCorreo(auth.getName()).getIdCliente();
    return "redirect:/clientes/editar/" + id;
}
  
}