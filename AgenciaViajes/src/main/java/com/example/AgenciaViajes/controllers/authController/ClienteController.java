package com.example.AgenciaViajes.controllers.authController;



import com.example.AgenciaViajes.dto.RegistroClienteDTO;
import com.example.AgenciaViajes.servicios.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // 1. LISTAR TODOS LOS CLIENTES
    @GetMapping("/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("titulo", "Lista de Clientes");
        return "cliente/Lista"; // Nombre de tu vista HTML
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
            return "redirect:/ingreso/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/cliente/register";
        }
    }

    // 4. FORMULARIO DE EDICIÓN
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        // Obtenemos los datos actuales y los enviamos al formulario
        model.addAttribute("cliente", clienteService.obtenerTodos()
                .stream()
                .filter(c -> c.getIdCliente().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
        model.addAttribute("titulo", "Editar Cliente");
        return "cliente/Formulario";
    }

    // 5. ACTUALIZAR (Procesado mediante POST desde el formulario)
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute("cliente") RegistroClienteDTO dto) {
        clienteService.actualizarPerfil(id, dto);
        return "redirect:/clientes"; // Redirige a la lista
    }
}
