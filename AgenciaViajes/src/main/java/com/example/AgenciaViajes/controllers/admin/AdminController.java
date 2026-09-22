package com.example.AgenciaViajes.controllers.admin;

import com.example.AgenciaViajes.dto.EntidadConfigDTO;
import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.repositorio.AerolineaRepository;
import com.example.AgenciaViajes.repositorio.CategoriaDestinoRepository;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import com.example.AgenciaViajes.repositorio.HotelRepository;
import com.example.AgenciaViajes.repositorio.PaqueteRepository;
import com.example.AgenciaViajes.repositorio.ReservaRepository;
import com.example.AgenciaViajes.repositorio.RolRepository;
import com.example.AgenciaViajes.repositorio.UsuarioRepository;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ClienteRepository clienteRepository;
    private final PaqueteRepository paqueteRepository;
    private final DestinoRepository destinoRepository;
    private final CategoriaDestinoRepository categoriaDestinoRepository;
    private final HotelRepository hotelRepository;
    private final AerolineaRepository aerolineaRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public AdminController(ClienteRepository clienteRepository,
                           PaqueteRepository paqueteRepository,
                           DestinoRepository destinoRepository,
                           CategoriaDestinoRepository categoriaDestinoRepository,
                           HotelRepository hotelRepository,
                           AerolineaRepository aerolineaRepository,
                           RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           ReservaRepository reservaRepository) {
        this.clienteRepository = clienteRepository;
        this.paqueteRepository = paqueteRepository;
        this.destinoRepository = destinoRepository;
        this.categoriaDestinoRepository = categoriaDestinoRepository;
        this.hotelRepository = hotelRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    private void agregarDatosAdmin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(auth.getName());
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
                .totalRegistros(paqueteRepository.count())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Destinos")
                .descripcion("Ciudades colombianas, descripciones y galerías fotográficas.")
                .icono("fa-solid fa-location-dot")
                .url("/admin/destinos")
                .totalRegistros(destinoRepository.count())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Categorías")
                .descripcion("Clasificación tipológica: Playa, Montaña, Ecoturismo y Aventura.")
                .icono("fa-solid fa-layer-group")
                .url("/admin/categorias")
                .totalRegistros(categoriaDestinoRepository.count())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Hoteles")
                .descripcion("Alojamientos asociados, estrellas, destinos y tarifas extra.")
                .icono("fa-solid fa-hotel")
                .url("/admin/hoteles")
                .totalRegistros(hotelRepository.count())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Aerolíneas")
                .descripcion("Compañías aéreas, códigos IATA y suplementos de vuelo.")
                .icono("fa-solid fa-plane-departure")
                .url("/admin/aerolineas")
                .totalRegistros(aerolineaRepository.count())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Clientes")
                .descripcion("Padrón de usuarios, documentos de identidad y contactos.")
                .icono("fa-solid fa-users")
                .url("/admin/clientes")
                .totalRegistros(clienteRepository.count())
                .sufijo("reg.")
                .build());

        entidades.add(EntidadConfigDTO.builder()
                .nombre("Roles")
                .descripcion("Niveles de acceso, permisos y cuentas del sistema.")
                .icono("fa-solid fa-shield-halved")
                .url("/admin/roles")
                .totalRegistros(rolRepository.count())
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
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "admin/usuarios";
    }

    // ==========================================
    // CRUD PAQUETES
    // ==========================================
    @GetMapping("/paquetes")
    public String paquetes(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("paquetes", paqueteRepository.findAll());
        model.addAttribute("totalPaquetes", paqueteRepository.count());
        return "admin/paquetes";
    }

    @GetMapping("/paquetes/nuevo")
    public String nuevoPaquete(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Paquete paquete = new Paquete();
        paquete.setEstado(EstadoGeneral.ACTIVO);
        model.addAttribute("paquete", paquete);
        model.addAttribute("destinos", destinoRepository.findAll());
        model.addAttribute("hoteles", hotelRepository.findAll());
        model.addAttribute("aerolineas", aerolineaRepository.findAll());
        return "admin/paquetes-form";
    }

    @GetMapping("/paquetes/editar/{id}")
    public String editarPaquete(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Paquete paquete = paqueteRepository.findById(id).orElse(new Paquete());
        model.addAttribute("paquete", paquete);
        model.addAttribute("destinos", destinoRepository.findAll());
        model.addAttribute("hoteles", hotelRepository.findAll());
        model.addAttribute("aerolineas", aerolineaRepository.findAll());
        return "admin/paquetes-form";
    }

    @PostMapping("/paquetes/guardar")
    public String guardarPaquete(@ModelAttribute Paquete paquete,
                                 @RequestParam(value = "incluye", required = false) List<String> incluye,
                                 @RequestParam(value = "itinerario", required = false) List<String> itinerario) {
        if (paquete.getDestino() != null && paquete.getDestino().getIdDestino() != null) {
            destinoRepository.findById(paquete.getDestino().getIdDestino()).ifPresent(paquete::setDestino);
        }
        if (paquete.getAerolinea() != null && paquete.getAerolinea().getIdAerolinea() != null) {
            aerolineaRepository.findById(paquete.getAerolinea().getIdAerolinea()).ifPresent(paquete::setAerolinea);
        } else {
            paquete.setAerolinea(null);
        }
        if (paquete.getHotel() != null && paquete.getHotel().getIdHotel() != null) {
            hotelRepository.findById(paquete.getHotel().getIdHotel()).ifPresent(paquete::setHotel);
        } else {
            paquete.setHotel(null);
        }

        if (incluye != null) {
            paquete.setIncluye(incluye.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.toList()));
        } else {
            paquete.setIncluye(new ArrayList<>());
        }

        if (itinerario != null) {
            paquete.setItinerario(itinerario.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.toList()));
        } else {
            paquete.setItinerario(new ArrayList<>());
        }

        if (paquete.getIdPaquete() != null) {
            paqueteRepository.findById(paquete.getIdPaquete()).ifPresent(existente -> {
                paquete.setFechaRegistro(existente.getFechaRegistro());
            });
        }

        paqueteRepository.save(paquete);
        return "redirect:/admin/paquetes";
    }

    @GetMapping("/paquetes/eliminar/{id}")
    public String eliminarPaquete(@PathVariable Long id) {
        paqueteRepository.deleteById(id);
        return "redirect:/admin/paquetes";
    }

    // ==========================================
    // CRUD HOTELES
    // ==========================================
    @GetMapping("/hoteles")
    public String hoteles(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("hoteles", hotelRepository.findAll());
        model.addAttribute("totalHoteles", hotelRepository.count());
        return "admin/hoteles";
    }

    @GetMapping("/hoteles/nuevo")
    public String nuevoHotel(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Hotel hotel = new Hotel();
        hotel.setEstado(EstadoGeneral.ACTIVO);
        model.addAttribute("hotel", hotel);
        model.addAttribute("destinos", destinoRepository.findAll());
        return "admin/hoteles-form";
    }

    @GetMapping("/hoteles/editar/{id}")
    public String editarHotel(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Hotel hotel = hotelRepository.findById(id).orElse(new Hotel());
        model.addAttribute("hotel", hotel);
        model.addAttribute("destinos", destinoRepository.findAll());
        return "admin/hoteles-form";
    }

    @PostMapping("/hoteles/guardar")
    public String guardarHotel(@ModelAttribute Hotel hotel) {
        if (hotel.getDestino() != null && hotel.getDestino().getIdDestino() != null) {
            destinoRepository.findById(hotel.getDestino().getIdDestino()).ifPresent(hotel::setDestino);
        }
        hotelRepository.save(hotel);
        return "redirect:/admin/hoteles";
    }

    @GetMapping("/hoteles/eliminar/{id}")
    public String eliminarHotel(@PathVariable Long id) {
        hotelRepository.deleteById(id);
        return "redirect:/admin/hoteles";
    }

    // ==========================================
    // CRUD AEROLÍNEAS
    // ==========================================
    @GetMapping("/aerolineas")
    public String aerolineas(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("aerolineas", aerolineaRepository.findAll());
        model.addAttribute("totalAerolineas", aerolineaRepository.count());
        return "admin/aerolineas";
    }

    @GetMapping({"/aerolineas/nuevo", "/aerolineas/nueva"})
    public String nuevaAerolinea(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("aerolinea", new Aerolinea());
        return "admin/aerolineas-form";
    }

    @GetMapping("/aerolineas/editar/{id}")
    public String editarAerolinea(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        Aerolinea aero = aerolineaRepository.findById(id).orElse(new Aerolinea());
        model.addAttribute("aerolinea", aero);
        return "admin/aerolineas-form";
    }

    @PostMapping("/aerolineas/guardar")
    public String guardarAerolinea(@ModelAttribute Aerolinea aerolinea) {
        aerolineaRepository.save(aerolinea);
        return "redirect:/admin/aerolineas";
    }

    @GetMapping("/aerolineas/eliminar/{id}")
    public String eliminarAerolinea(@PathVariable Long id) {
        aerolineaRepository.deleteById(id);
        return "redirect:/admin/aerolineas";
    }

    // ==========================================
    // OTRAS VISTAS (DESTINOS, CATEGORÍAS, CLIENTES, ROLES, RESERVAS)
    // ==========================================
    @GetMapping("/destinos")
    public String destinos(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("destinos", destinoRepository.findAll());
        model.addAttribute("totalDestinos", destinoRepository.count());
        return "admin/destinos";
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("categorias", categoriaDestinoRepository.findAll());
        model.addAttribute("totalCategorias", categoriaDestinoRepository.count());
        return "admin/categorias";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("totalClientes", clienteRepository.count());
        return "admin/clientes";
    }

    @GetMapping("/roles")
    public String roles(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("totalRoles", rolRepository.count());
        return "admin/roles";
    }

    @GetMapping("/reservas")
    public String reservas(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "reservas");

        List<Reserva> reservas = reservaRepository.findAllByOrderByFechaReservaDesc();
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
