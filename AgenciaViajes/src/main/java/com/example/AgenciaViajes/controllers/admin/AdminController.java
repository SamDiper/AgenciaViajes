package com.example.AgenciaViajes.controllers.admin;

import com.example.AgenciaViajes.dto.EntidadConfigDTO;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.repositorio.AerolineaRepository;
import com.example.AgenciaViajes.repositorio.CategoriaDestinoRepository;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import com.example.AgenciaViajes.repositorio.HotelRepository;
import com.example.AgenciaViajes.repositorio.PaqueteRepository;
import com.example.AgenciaViajes.repositorio.RolRepository;
import com.example.AgenciaViajes.repositorio.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public AdminController(ClienteRepository clienteRepository,
                           PaqueteRepository paqueteRepository,
                           DestinoRepository destinoRepository,
                           CategoriaDestinoRepository categoriaDestinoRepository,
                           HotelRepository hotelRepository,
                           AerolineaRepository aerolineaRepository,
                           RolRepository rolRepository,
                           UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.paqueteRepository = paqueteRepository;
        this.destinoRepository = destinoRepository;
        this.categoriaDestinoRepository = categoriaDestinoRepository;
        this.hotelRepository = hotelRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
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

    @GetMapping("/paquetes")
    public String paquetes(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("paquetes", paqueteRepository.findAll());
        model.addAttribute("totalPaquetes", paqueteRepository.count());
        return "admin/paquetes";
    }

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

    @GetMapping("/hoteles")
    public String hoteles(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("hoteles", hotelRepository.findAll());
        model.addAttribute("totalHoteles", hotelRepository.count());
        return "admin/hoteles";
    }

    @GetMapping("/aerolineas")
    public String aerolineas(Model model) {
        agregarDatosAdmin(model);
        model.addAttribute("activeSection", "configuracion");
        model.addAttribute("aerolineas", aerolineaRepository.findAll());
        model.addAttribute("totalAerolineas", aerolineaRepository.count());
        return "admin/aerolineas";
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
}
