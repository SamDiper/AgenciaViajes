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

    @GetMapping("/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("titulo", "Lista de Clientes");
        return "auto/cliente/Lista";
    }

    @GetMapping
    public String nuevoForm(Model model) {
        model.addAttribute("registroClienteDTO", new RegistroClienteDTO());
        model.addAttribute("titulo", "Registrar Cliente");
        return "auth/cliente/register";
    }

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
            model.addAttribute("errorMessage", "Este correo ya está registrado");
            return "auth/cliente/register";
        }
    }

@GetMapping("/editar/{id}")
public String editarForm(@PathVariable Integer id, Authentication auth, Model model) {
    Cliente cliente = clienteService.obtenerPorCorreo(auth.getName());
    if (!cliente.getIdCliente().equals(id)) {
        return "redirect:/clientes/mi-perfil";
    }

    RegistroClienteDTO dto = new RegistroClienteDTO();
    dto.setNombreCompleto(cliente.getNombreCompleto());
    dto.setTipoDocumento(cliente.getTipoDocumento().name());
    dto.setDocumento(cliente.getDocumento());
    dto.setCorreo(cliente.getCorreo());
    dto.setTelefono(cliente.getTelefono());
    dto.setDireccion(cliente.getDireccion());

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

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute("registroClienteDTO") RegistroClienteDTO dto,
                             Authentication auth,
                             RedirectAttributes flash) {
        Cliente logueado = clienteService.obtenerPorCorreo(auth.getName());
        if (!logueado.getIdCliente().equals(id)) {
            flash.addFlashAttribute("error", "No puedes editar los datos de otro usuario.");
            return "redirect:/clientes/mi-perfil";
        }

        clienteService.actualizarPerfil(id, dto);
        flash.addFlashAttribute("exito", "Tus datos se actualizaron correctamente.");
        return "redirect:/clientes/editar/" + id;
    }

    @GetMapping("/mi-perfil")
public String miPerfil(Authentication auth) {
    Integer id = clienteService.obtenerPorCorreo(auth.getName()).getIdCliente();
    return "redirect:/clientes/editar/" + id;
}
  
}