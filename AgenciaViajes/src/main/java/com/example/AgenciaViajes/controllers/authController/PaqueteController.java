package com.example.AgenciaViajes.controllers.authController;

import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import com.example.AgenciaViajes.servicios.PaqueteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/paquetes")
public class PaqueteController {

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private DestinoRepository destinoRepository;

    /** Catálogo completo — muestra todos los paquetes activos */
    @GetMapping
    public String catalogo(Model model) {
        List<Paquete> paquetes = paqueteService.listarActivos();
        List<Destino> destinos = destinoRepository.findAll();

        model.addAttribute("paquetes", paquetes);
        model.addAttribute("destinos", destinos);
        model.addAttribute("totalResultados", paquetes.size());
        return "auth/Paquetes/paquetes";
    }

    /** Búsqueda con filtros opcionales por destino, precio y duración */
    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer durMin,
            @RequestParam(required = false) Integer durMax,
            Model model) {

        List<Paquete> paquetes = paqueteService.buscar(destino, precioMin, precioMax, durMin, durMax);
        List<Destino> destinos = destinoRepository.findAll();

        model.addAttribute("paquetes", paquetes);
        model.addAttribute("destinos", destinos);
        model.addAttribute("totalResultados", paquetes.size());

        // Reenviar valores del formulario para mantenerlos visibles
        model.addAttribute("filtroDestino", destino);
        model.addAttribute("filtroPrecioMin", precioMin);
        model.addAttribute("filtroPrecioMax", precioMax);
        model.addAttribute("filtroDurMin", durMin);
        model.addAttribute("filtroDurMax", durMax);

        return "auth/Paquetes/paquetes";
    }
}
