package com.example.AgenciaViajes.controllers.admin;

import com.example.AgenciaViajes.dto.EntidadConfigDTO;
import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.CategoriaDestino;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.modelo.Rol;
import com.example.AgenciaViajes.modelo.Usuario;
import com.example.AgenciaViajes.servicios.AerolineaService;
import com.example.AgenciaViajes.servicios.CategoriaDestinoService;
import com.example.AgenciaViajes.servicios.ClienteAdminService;
import com.example.AgenciaViajes.servicios.ClienteService;
import com.example.AgenciaViajes.servicios.DestinoService;
import com.example.AgenciaViajes.servicios.Pago.FacturaService;
import com.example.AgenciaViajes.servicios.HotelService;
import com.example.AgenciaViajes.servicios.PaqueteService;
import com.example.AgenciaViajes.servicios.ReservaService;
import com.example.AgenciaViajes.servicios.RolService;
import com.example.AgenciaViajes.servicios.UsuarioAdminService;
import com.example.AgenciaViajes.servicios.UsuarioService;
import com.example.AgenciaViajes.servicios.Pdf.PdfService;
import com.example.AgenciaViajes.modelo.Factura;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.AgenciaViajes.servicios.DashboardService;
import com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

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
    private final ClienteAdminService clienteAdminService;
    private final UsuarioAdminService usuarioAdminService;
    private final FacturaService facturaService;
    private final PdfService pdfService;
    private final DashboardService dashboardService;

    public AdminController(ClienteService clienteService,
                           PaqueteService paqueteService,
                           DestinoService destinoService,
                           CategoriaDestinoService categoriaDestinoService,
                           HotelService hotelService,
                           AerolineaService aerolineaService,
                           RolService rolService,
                           UsuarioService usuarioService,
                           ReservaService reservaService,
                           ClienteAdminService clienteAdminService,
                           UsuarioAdminService usuarioAdminService,
                           FacturaService facturaService,
                           PdfService pdfService,
                           DashboardService dashboardService) {
        this.clienteService = clienteService;
        this.paqueteService = paqueteService;
        this.destinoService = destinoService;
        this.categoriaDestinoService = categoriaDestinoService;
        this.hotelService = hotelService;
        this.aerolineaService = aerolineaService;
        this.rolService = rolService;
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
        this.clienteAdminService = clienteAdminService;
        this.usuarioAdminService = usuarioAdminService;
        this.facturaService = facturaService;
        this.pdfService = pdfService;
        this.dashboardService = dashboardService;
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
    public String index(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idCategoria,
            Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "dashboard");

        DashboardStatsDTO stats = dashboardService.obtenerEstadisticas(fechaInicio, fechaFin, idCategoria);
        model.addAttribute("stats", stats);
        model.addAttribute("categorias", categoriaDestinoService.listarTodas());
        model.addAttribute("fechaInicioFiltro", fechaInicio);
        model.addAttribute("fechaFinFiltro", fechaFin);
        model.addAttribute("idCategoriaFiltro", idCategoria);

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

    @GetMapping({"/usuarios/nuevo", "/usuarios/nueva"})
    public String nuevoUsuario(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "usuarios");
        model.addAttribute("usuarioForm", new Usuario());
        return "admin/usuarios/formulario";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@RequestParam String nombreUsuario,
                                 @RequestParam String correo,
                                 @RequestParam String contrasena) {
        usuarioAdminService.crear(nombreUsuario, correo, contrasena);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/ver/{id}")
    public String verUsuario(@PathVariable Integer id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "usuarios");
        model.addAttribute("usuarioDetalle", usuarioAdminService.buscarPorId(id));
        return "admin/usuarios/ver";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "usuarios");
        model.addAttribute("usuarioForm", usuarioAdminService.buscarPorId(id));
        return "admin/usuarios/formulario";
    }

    @PostMapping("/usuarios/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Integer id,
                                    @RequestParam String nombreUsuario,
                                    @RequestParam String correo,
                                    @RequestParam(required = false) String contrasena,
                                    @RequestParam(required = false) EstadoGeneral estado) {
        usuarioAdminService.actualizar(id, nombreUsuario, correo, contrasena, estado);
        return "redirect:/admin/usuarios";
    }

    @RequestMapping(value = "/usuarios/eliminar/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioAdminService.eliminar(id);
        return "redirect:/admin/usuarios";
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
    // RESERVAS
    // ==========================================
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

    // ==========================================
    // FACTURACIÓN
    // ==========================================
    @GetMapping("/facturas")
    public String facturas(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "facturas");

        List<Factura> facturas = facturaService.listarTodas();
        BigDecimal totalFacturado = facturas.stream()
                .map(Factura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIva = facturas.stream()
                .map(Factura::getIva)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("facturas", facturas);
        model.addAttribute("totalFacturas", facturas.size());
        model.addAttribute("totalFacturado", totalFacturado);
        model.addAttribute("totalIva", totalIva);
        return "admin/factura/facturas";
    }

    @GetMapping("/facturas/{id}/pdf")
    public ResponseEntity<byte[]> descargarFacturaAdmin(@PathVariable Integer id) {
        Factura factura = facturaService.buscarPorId(id);
        byte[] pdf = pdfService.generarFactura(factura);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=factura_" + factura.getNumero() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ==========================================
    // CRUD DESTINOS
    // ==========================================
    @GetMapping("/destinos")
    public String destinos(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("destinos", destinoService.listarTodos());
        model.addAttribute("totalDestinos", destinoService.contar());
        return "admin/destinos";
    }

    @GetMapping({"/destinos/nuevo", "/destinos/nueva"})
    public String nuevoDestino(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("destino", new Destino());
        model.addAttribute("categorias", categoriaDestinoService.listarTodas());
        return "admin/destinos/formulario";
    }

    @GetMapping("/destinos/ver/{id}")
    public String verDestino(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("destino", destinoService.buscarPorId(id));
        return "admin/destinos/ver";
    }

    @GetMapping("/destinos/editar/{id}")
    public String editarDestino(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("destino", destinoService.buscarPorId(id));
        model.addAttribute("categorias", categoriaDestinoService.listarTodas());
        return "admin/destinos/formulario";
    }

    @PostMapping("/destinos/guardar")
    public String guardarDestino(@ModelAttribute Destino destino,
                                 @RequestParam("idCategoria") Long idCategoria) {
        CategoriaDestino categoria = categoriaDestinoService.buscarPorId(idCategoria);

        if (destino.getIdDestino() == null) {
            // Crear: se guarda lo que viene del formulario
            if (destino.getEstado() == null) {
                destino.setEstado(EstadoGeneral.ACTIVO);
            }
            destino.setCategoria(categoria);
            destinoService.guardar(destino);
        } else {
            // Editar: se copian los datos del formulario sobre el destino que ya existe,
            // así no se pierden campos como la fecha de registro
            Destino existente = destinoService.buscarPorId(destino.getIdDestino());
            existente.setNombre(destino.getNombre());
            existente.setCiudad(destino.getCiudad());
            existente.setPais(destino.getPais());
            existente.setDescripcion(destino.getDescripcion());
            existente.setImagenUrl(destino.getImagenUrl());
            if (destino.getEstado() != null) {
                existente.setEstado(destino.getEstado());
            }
            existente.setCategoria(categoria);
            destinoService.guardar(existente);
        }
        return "redirect:/admin/destinos";
    }

    @RequestMapping(value = "/destinos/eliminar/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String eliminarDestino(@PathVariable Long id) {
        destinoService.eliminar(id);
        return "redirect:/admin/destinos";
    }

    // ==========================================
    // CRUD CATEGORÍAS
    // ==========================================
    @GetMapping("/categorias")
    public String categorias(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("categorias", categoriaDestinoService.listarTodas());
        model.addAttribute("totalCategorias", categoriaDestinoService.contar());
        return "admin/categorias";
    }

    @GetMapping({"/categorias/nuevo", "/categorias/nueva"})
    public String nuevaCategoria(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("categoria", new CategoriaDestino());
        return "admin/categorias/formulario";
    }

    @GetMapping("/categorias/ver/{id}")
    public String verCategoria(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("categoria", categoriaDestinoService.buscarPorId(id));
        return "admin/categorias/ver";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("categoria", categoriaDestinoService.buscarPorId(id));
        return "admin/categorias/formulario";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@RequestParam(required = false) Long idCategoria,
                                   @RequestParam String nombre) {
        if (idCategoria == null) {
            categoriaDestinoService.crear(nombre);
        } else {
            categoriaDestinoService.actualizar(idCategoria, nombre);
        }
        return "redirect:/admin/categorias";
    }

    @RequestMapping(value = "/categorias/eliminar/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaDestinoService.eliminar(id);
        return "redirect:/admin/categorias";
    }

    // ==========================================
    // CRUD CLIENTES
    // ==========================================
    @GetMapping("/clientes")
    public String clientes(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("totalClientes", clienteService.contar());
        return "admin/clientes";
    }

    @GetMapping({"/clientes/nuevo", "/clientes/nueva"})
    public String nuevoCliente(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("clienteForm", new Cliente());
        return "admin/clientes/formulario";
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente,
                                 @RequestParam String contrasena) {
        clienteAdminService.crear(cliente, contrasena);
        return "redirect:/admin/clientes";
    }

    @GetMapping("/clientes/ver/{id}")
    public String verCliente(@PathVariable Integer id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("clienteDetalle", clienteAdminService.buscarPorId(id));
        return "admin/clientes/ver";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(@PathVariable Integer id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("clienteForm", clienteAdminService.buscarPorId(id));
        return "admin/clientes/formulario";
    }

    @PostMapping("/clientes/actualizar/{id}")
    public String actualizarCliente(@PathVariable Integer id, @ModelAttribute Cliente cliente) {
        clienteAdminService.actualizar(id, cliente);
        return "redirect:/admin/clientes";
    }

    // Acepta GET y POST, así funciona tanto con un enlace como con un formulario
    @RequestMapping(value = "/clientes/eliminar/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String eliminarCliente(@PathVariable Integer id) {
        clienteAdminService.eliminar(id);
        return "redirect:/admin/clientes";
    }

    // ==========================================
    // CRUD ROLES
    // ==========================================
    @GetMapping("/roles")
    public String roles(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("roles", rolService.listarTodos());
        model.addAttribute("totalRoles", rolService.contar());
        return "admin/roles";
    }

    @GetMapping({"/roles/nuevo", "/roles/nueva"})
    public String nuevoRol(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("rol", new Rol());
        return "admin/roles/formulario";
    }

    @GetMapping("/roles/ver/{id}")
    public String verRol(@PathVariable Integer id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("rol", rolService.buscarPorId(id));
        return "admin/roles/ver";
    }

    @GetMapping("/roles/editar/{id}")
    public String editarRol(@PathVariable Integer id, Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("rol", rolService.buscarPorId(id));
        return "admin/roles/formulario";
    }

    @PostMapping("/roles/guardar")
    public String guardarRol(@RequestParam(required = false) Integer idRol,
                             @RequestParam String nombreRol) {
        if (idRol == null) {
            rolService.crear(nombreRol);
        } else {
            rolService.actualizar(idRol, nombreRol);
        }
        return "redirect:/admin/roles";
    }

    @RequestMapping(value = "/roles/eliminar/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String eliminarRol(@PathVariable Integer id) {
        rolService.eliminar(id);
        return "redirect:/admin/roles";
    }
}
