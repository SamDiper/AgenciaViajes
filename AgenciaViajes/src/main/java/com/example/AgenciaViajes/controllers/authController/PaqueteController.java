package com.example.AgenciaViajes.controllers.authController;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.servicios.AerolineaService;
import com.example.AgenciaViajes.servicios.DestinoService;
import com.example.AgenciaViajes.servicios.HotelService;
import com.example.AgenciaViajes.servicios.PaqueteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/paquetes")
public class PaqueteController {

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private DestinoService destinoService;

    @Autowired
    private AerolineaService aerolineaService;

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public String catalogo(Model model) {
        List<Paquete> paquetes = paqueteService.listarActivos();
        List<Destino> destinos = destinoService.listarTodos();

        model.addAttribute("paquetes", paquetes);
        model.addAttribute("destinos", destinos);
        model.addAttribute("totalResultados", paquetes.size());
        return "auth/Paquetes/paquetes";
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer durMin,
            @RequestParam(required = false) Integer durMax,
            Model model) {

        List<Paquete> paquetes = paqueteService.buscar(destino, precioMin, precioMax, durMin, durMax);
        List<Destino> destinos = destinoService.listarTodos();

        model.addAttribute("paquetes", paquetes);
        model.addAttribute("destinos", destinos);
        model.addAttribute("totalResultados", paquetes.size());

        model.addAttribute("filtroDestino", destino);
        model.addAttribute("filtroPrecioMin", precioMin);
        model.addAttribute("filtroPrecioMax", precioMax);
        model.addAttribute("filtroDurMin", durMin);
        model.addAttribute("filtroDurMax", durMax);

        return "auth/Paquetes/paquetes";
    }

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer cantidadBoletos,
            @RequestParam(required = false) Long idAerolinea,
            @RequestParam(required = false) Long idHotel,
            @RequestParam(required = false, defaultValue = "false") Boolean sinVinculacion,
            Model model) {

        Optional<Paquete> paqueteOpt = paqueteService.obtenerPorId(id);
        if (paqueteOpt.isEmpty()) {
            return "redirect:/paquetes";
        }

        Paquete paquete = paqueteOpt.get();

        if (cantidadBoletos == null || cantidadBoletos < 1) {
            cantidadBoletos = 1;
        }

        List<Aerolinea> aerolineas = List.of();
        if (paquete.getDestino() != null && paquete.getDestino().getIdDestino() != null) {
            aerolineas = aerolineaService.listarPorDestino(paquete.getDestino().getIdDestino());
        }
        if (paquete.getAerolinea() != null) {
            boolean contieneAero = aerolineas.stream().anyMatch(a -> a.getIdAerolinea().equals(paquete.getAerolinea().getIdAerolinea()));
            if (!contieneAero) {
                aerolineas = new java.util.ArrayList<>(aerolineas);
                aerolineas.add(0, paquete.getAerolinea());
            }
        }
        if (aerolineas.isEmpty() && paquete.getAerolinea() != null) {
            aerolineas = List.of(paquete.getAerolinea());
        }

        Aerolinea aerolineaMasEconomica = aerolineas.stream()
                .min(Comparator
                        .comparing(a -> a.getPrecioAdicional() != null ? a.getPrecioAdicional() : BigDecimal.ZERO))
                .orElse(null);

        List<Hotel> hotelesDestino = List.of();
        if (paquete.getHotel() != null) {
            hotelesDestino = List.of(paquete.getHotel());
        }

        Aerolinea aerolineaSeleccionada = null;
        if (idAerolinea != null) {
            aerolineaSeleccionada = aerolineas.stream()
                    .filter(a -> a.getIdAerolinea().equals(idAerolinea))
                    .findFirst()
                    .orElse(null);
        }
        if (aerolineaSeleccionada == null) {
            aerolineaSeleccionada = paquete.getAerolinea() != null ? paquete.getAerolinea() : aerolineaMasEconomica;
        }

        Hotel hotelSeleccionado = null;
        if (!sinVinculacion) {
            if (idHotel != null && idHotel > 0) {
                hotelSeleccionado = hotelesDestino.stream()
                        .filter(h -> h.getIdHotel().equals(idHotel))
                        .findFirst()
                        .orElse(null);
            } else if (idHotel == null) {
                hotelSeleccionado = paquete.getHotel();
            }
        }

        List<Paquete> paquetesRelacionados = paqueteService.listarActivos().stream()
                .filter(p -> p.getDestino() != null
                        && p.getDestino().getIdDestino().equals(paquete.getDestino().getIdDestino())
                        && !p.getIdPaquete().equals(paquete.getIdPaquete()))
                .collect(Collectors.toList());

        BigDecimal precioBaseUnitario = paquete.getPrecioBase();
        BigDecimal adicionalAerolinea = (aerolineaSeleccionada != null
                && aerolineaSeleccionada.getPrecioAdicional() != null)
                        ? aerolineaSeleccionada.getPrecioAdicional()
                        : BigDecimal.ZERO;

        BigDecimal adicionalHotel = (hotelSeleccionado != null && hotelSeleccionado.getPrecioAdicional() != null
                && !sinVinculacion)
                        ? hotelSeleccionado.getPrecioAdicional()
                        : BigDecimal.ZERO;

        BigDecimal precioUnitarioTotal = precioBaseUnitario.add(adicionalAerolinea).add(adicionalHotel);
        BigDecimal precioGranTotal = precioUnitarioTotal.multiply(BigDecimal.valueOf(cantidadBoletos));

        model.addAttribute("paquete", paquete);
        model.addAttribute("aerolineas", aerolineas);
        model.addAttribute("aerolineaMasEconomica", aerolineaMasEconomica);
        model.addAttribute("hotelesDestino", hotelesDestino);
        model.addAttribute("paquetesRelacionados", paquetesRelacionados);

        model.addAttribute("cantidadBoletos", cantidadBoletos);
        model.addAttribute("aerolineaSeleccionada", aerolineaSeleccionada);
        model.addAttribute("hotelSeleccionado", hotelSeleccionado);
        model.addAttribute("sinVinculacion", sinVinculacion);

        model.addAttribute("precioBaseUnitario", precioBaseUnitario);
        model.addAttribute("adicionalAerolinea", adicionalAerolinea);
        model.addAttribute("adicionalHotel", adicionalHotel);
        model.addAttribute("precioUnitarioTotal", precioUnitarioTotal);
        model.addAttribute("precioGranTotal", precioGranTotal);

        return "auth/Paquetes/detalle";
    }
}
