package com.example.AgenciaViajes.controllers.admin;

import com.example.AgenciaViajes.dto.EntidadConfigDTO;
import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.servicios.AerolineaService;
import com.example.AgenciaViajes.servicios.CategoriaDestinoService;
import com.example.AgenciaViajes.servicios.ClienteService;
import com.example.AgenciaViajes.servicios.DestinoService;
import com.example.AgenciaViajes.servicios.HotelService;
import com.example.AgenciaViajes.servicios.PaqueteService;
import com.example.AgenciaViajes.servicios.ReservaService;
import com.example.AgenciaViajes.servicios.RolService;
import com.example.AgenciaViajes.servicios.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ClienteService clienteService;
    private final PaqueteService paqueteService;
    private final DestinoService destinoService;
    private final CategoriaDestinoService categoriaDestinoService;
    private final HotelService hotelService;
    private final AerolineaService aerolineaService;
    private final RolService rolService;
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;

    public AdminController(ClienteService clienteService,
                           PaqueteService paqueteService,
                           DestinoService destinoService,
                           CategoriaDestinoService categoriaDestinoService,
                           HotelService hotelService,
                           AerolineaService aerolineaService,
                           RolService rolService,
                           UsuarioService usuarioService,
                           ReservaService reservaService) {
        this.clienteService = clienteService;
        this.paqueteService = paqueteService;
        this.destinoService = destinoService;
        this.categoriaDestinoService = categoriaDestinoService;
        this.hotelService = hotelService;
        this.aerolineaService = aerolineaService;
        this.rolService = rolService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
    }

    private void agregarDatosAdmin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Optional<Cliente> clienteOpt = clienteService.buscarPorCorreoOpt(auth.getName());
            clienteOpt.ifPresent(cliente -> {
                model.addAttribute("cliente", cliente);
                model.addAttribute("nombreAdmin", cliente.getNombreCompleto());
                model.addAttribute("correoAdmin", cliente.getCorreo());
            });
        }
    }

    @GetMapping({"", "/", "/dashboard"})
    public String index(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "dashboard");
        return "admin/index";
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        
        List<EntidadConfigDTO> entidades = new ArrayList<>();

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Paquetes")
                .descripcion("Planes turísticos, itinerarios, inclusión de servicios y precios base.")
                .icono("fa-solid fa-suitcase")
                .url("/admin/paquetes")
                .totalRegistros(paqueteService.contar())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Destinos")
                .descripcion("Ciudades colombianas, descripciones y galerías fotográficas.")
                .icono("fa-solid fa-location-dot")
                .url("/admin/destinos")
                .totalRegistros(destinoService.contar())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Categorías")
                .descripcion("Clasificación tipológica: Playa, Montaña, Ecoturismo y Aventura.")
                .icono("fa-solid fa-layer-group")
                .url("/admin/categorias")
                .totalRegistros(categoriaDestinoService.contar())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Hoteles")
                .descripcion("Alojamientos asociados, estrellas, destinos y tarifas extra.")
                .icono("fa-solid fa-hotel")
                .url("/admin/hoteles")
                .totalRegistros(hotelService.contar())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Aerolíneas")
                .descripcion("Compañías aéreas, códigos IATA y suplementos de vuelo.")
                .icono("fa-solid fa-plane-departure")
                .url("/admin/aerolineas")
                .totalRegistros(aerolineaService.contar())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Clientes")
                .descripcion("Padrón de usuarios, documentos de identidad y contactos.")
                .icono("fa-solid fa-users")
                .url("/admin/clientes")
                .totalRegistros(clienteService.contar())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Roles")
                .descripcion("Niveles de acceso, permisos y cuentas del sistema.")
                .icono("fa-solid fa-shield-halved")
                .url("/admin/roles")
                .totalRegistros(rolService.contar())
                .sufijo("roles")
                .build());

        model.addAttribute("entidadesConfig", entidades);
        model.addAttribute("totalEntidades", entidades.size());

        return "admin/configuracion";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "usuarios");
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("totalUsuarios", usuarioService.contar());
        return "admin/usuarios";
    }

    // ==========================================
    // CRUD PAQUETES
    // ==========================================
    @GetMapping("/paquetes")
    public String paquetes(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("paquetes", paqueteService.listarTodos());
        model.addAttribute("totalPaquetes", paqueteService.contar());
        return "admin/paquetes";
    }

    @GetMapping("/paquetes/nuevo")
    public String nuevoPaquete(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Paquete paquete = new Paquete();
        paquete.setEstado(EstadoGeneral.ACTIVO);
        model.addAttribute("paquete", paquete);
        model.addAttribute("destinos", destinoService.listarTodos());
        model.addAttribute("hoteles", hotelService.listarTodos());
        model.addAttribute("aerolineas", aerolineaService.listarTodas());
        return "admin/paquetes-form";
    }

    @GetMapping("/paquetes/editar/{id}")
    public String editarPaquete(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Paquete paquete = paqueteService.obtenerPorId(id).orElse(new Paquete());
        model.addAttribute("paquete", paquete);
        model.addAttribute("destinos", destinoService.listarTodos());
        model.addAttribute("hoteles", hotelService.listarTodos());
        model.addAttribute("aerolineas", aerolineaService.listarTodas());
        return "admin/paquetes-form";
    }

    @PostMapping("/paquetes/guardar")
    public String guardarPaquete(@ModelAttribute Paquete paquete,
                                 @RequestParam(value = "incluye", required = false) List<String> incluye,
                                 @RequestParam(value = "itinerario", required = false) List<String> itinerario) {
        paqueteService.guardar(paquete, incluye, itinerario);
        return "redirect:/admin/paquetes";
    }

    @GetMapping("/paquetes/eliminar/{id}")
    public String eliminarPaquete(@PathVariable Long id) {
        paqueteService.eliminar(id);
        return "redirect:/admin/paquetes";
    }

    // ==========================================
    // CRUD HOTELES
    // ==========================================
    @GetMapping("/hoteles")
    public String hoteles(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("hoteles", hotelService.listarTodos());
        model.addAttribute("totalHoteles", hotelService.contar());
        return "admin/hoteles";
    }

    @GetMapping("/hoteles/nuevo")
    public String nuevoHotel(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Hotel hotel = new Hotel();
        hotel.setEstado(EstadoGeneral.ACTIVO);
        model.addAttribute("hotel", hotel);
        model.addAttribute("destinos", destinoService.listarTodos());
        return "admin/hoteles-form";
    }

    @GetMapping("/hoteles/editar/{id}")
    public String editarHotel(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Hotel hotel = hotelService.obtenerPorId(id).orElse(new Hotel());
        model.addAttribute("hotel", hotel);
        model.addAttribute("destinos", destinoService.listarTodos());
        return "admin/hoteles-form";
    }

    @PostMapping("/hoteles/guardar")
    public String guardarHotel(@ModelAttribute Hotel hotel) {
        hotelService.guardar(hotel);
        return "redirect:/admin/hoteles";
    }

    @GetMapping("/hoteles/eliminar/{id}")
    public String eliminarHotel(@PathVariable Long id) {
        hotelService.eliminar(id);
        return "redirect:/admin/hoteles";
    }

    // ==========================================
    // CRUD AEROLÍNEAS
    // ==========================================
    @GetMapping("/aerolineas")
    public String aerolineas(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("aerolineas", aerolineaService.listarTodas());
        model.addAttribute("totalAerolineas", aerolineaService.contar());
        return "admin/aerolineas";
    }

    @GetMapping({"/aerolineas/nuevo", "/aerolineas/nueva"})
    public String nuevaAerolinea(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("aerolinea", new Aerolinea());
        model.addAttribute("destinos", destinoService.listarTodos());
        return "admin/aerolineas-form";
    }

    @GetMapping("/aerolineas/editar/{id}")
    public String editarAerolinea(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Aerolinea aero = aerolineaService.obtenerPorId(id).orElse(new Aerolinea());
        model.addAttribute("aerolinea", aero);
        model.addAttribute("destinos", destinoService.listarTodos());
        return "admin/aerolineas-form";
    }

    @PostMapping("/aerolineas/guardar")
    public String guardarAerolinea(@ModelAttribute Aerolinea aerolinea,
                                   @RequestParam(value = "destinoIds", required = false) List<Long> destinoIds) {
        aerolineaService.guardar(aerolinea, destinoIds);
        return "redirect:/admin/aerolineas";
    }

    @GetMapping("/aerolineas/eliminar/{id}")
    public String eliminarAerolinea(@PathVariable Long id) {
        aerolineaService.eliminar(id);
        return "redirect:/admin/aerolineas";
    }

    // ==========================================
    // OTRAS VISTAS (DESTINOS, CATEGORÍAS, CLIENTES, ROLES, RESERVAS)
    // ==========================================
    @GetMapping("/destinos")
    public String destinos(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("destinos", destinoService.listarTodos());
        model.addAttribute("totalDestinos", destinoService.contar());
        return "admin/destinos";
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("categorias", categoriaDestinoService.listarTodas());
        model.addAttribute("totalCategorias", categoriaDestinoService.contar());
        return "admin/categorias";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("totalClientes", clienteService.contar());
        return "admin/clientes";
    }

    @GetMapping("/roles")
    public String roles(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("roles", rolService.listarTodos());
        model.addAttribute("totalRoles", rolService.contar());
        return "admin/roles";
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "reservas");

        List<Reserva> reservas = reservaService.listarTodasOrdenadasPorFecha();
        model.addAttribute("reservas", reservas);
        model.addAttribute("totalReservas", reservas.size());

        long totalPendientes = reservas.stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.PENDIENTE)
                .count();
        long totalConfirmadas = reservas.stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA)
                .count();
        long totalCanceladas = reservas.stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.CANCELADA)
                .count();
        BigDecimal montoTotal = reservas.stream()
                .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA && r.getTotal() != null)
                .map(Reserva::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalPendientes", totalPendientes);
        model.addAttribute("totalConfirmadas", totalConfirmadas);
        model.addAttribute("totalCanceladas", totalCanceladas);
        model.addAttribute("montoTotal", montoTotal);

        return "admin/reservas";
    }
}
