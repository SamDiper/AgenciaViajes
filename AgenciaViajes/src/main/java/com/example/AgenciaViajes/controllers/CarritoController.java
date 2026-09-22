package com.example.AgenciaViajes.controllers;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.modelo.carrito.Carrito;
import com.example.AgenciaViajes.repositorio.AerolineaRepository;
import com.example.AgenciaViajes.repositorio.HotelRepository;
import com.example.AgenciaViajes.repositorio.PaqueteRepository;
import com.example.AgenciaViajes.servicios.ReservaService;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private static final int MAX_PERSONAS = 20;

    private final Carrito carrito;
    private final PaqueteRepository paqueteRepo;
    private final AerolineaRepository aerolineaRepo;
    private final HotelRepository hotelRepo;
    private final ReservaService reservaService;

    public CarritoController(Carrito carrito,
                             PaqueteRepository paqueteRepo,
                             AerolineaRepository aerolineaRepo,
                             HotelRepository hotelRepo,
                             ReservaService reservaService) {
        this.carrito = carrito;
        this.paqueteRepo = paqueteRepo;
        this.aerolineaRepo = aerolineaRepo;
        this.hotelRepo = hotelRepo;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String ver(Model model) {
        model.addAttribute("carrito", carrito);
        model.addAttribute("hoy", LocalDate.now());
        return "auth/Carrito/carrito";
    }

    /** Lo llama el botón "Reservar ahora" de la página del paquete. */
    @PostMapping("/agregar")
    public String agregar(@RequestParam Long idPaquete,
                          @RequestParam(defaultValue = "1") int personas,
                          @RequestParam(required = false) Long idAerolinea,
                          @RequestParam(required = false) Long idHotel,
                          RedirectAttributes flash) {
        
        Paquete paquete = paqueteRepo.findById(idPaquete).orElse(null);
        if (paquete == null) {
            flash.addFlashAttribute("error", "El paquete no existe.");
            return "redirect:/carrito";
        }

        // 0 o null significa "sin aerolínea" / "sin hotel"
        Aerolinea aerolinea = (idAerolinea != null && idAerolinea > 0) ? aerolineaRepo.findById(idAerolinea).orElse(null) : null;
        Hotel hotel = (idHotel != null && idHotel > 0) ? hotelRepo.findById(idHotel).orElse(null) : null;

        carrito.agregar(paquete, aerolinea, hotel, personas);
        flash.addFlashAttribute("exito", "Paquete agregado al carrito.");
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam String clave,
                             @RequestParam int personas,
                             RedirectAttributes flash) {
        if (personas < 1 || personas > MAX_PERSONAS) {
            flash.addFlashAttribute("error",
                    "La cantidad de personas debe estar entre 1 y " + MAX_PERSONAS + ".");
        } else {
            carrito.actualizarPersonas(clave, personas);
        }
        return "redirect:/carrito";
    }

    @PostMapping("/quitar/{clave}")
    public String quitar(@PathVariable String clave, RedirectAttributes flash) {
        carrito.quitar(clave);
        flash.addFlashAttribute("exito", "Paquete eliminado del carrito.");
        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciar(RedirectAttributes flash) {
        carrito.vaciar();
        flash.addFlashAttribute("exito", "Carrito vaciado.");
        return "redirect:/carrito";
    }

    @PostMapping("/confirmar")
    public String confirmar(HttpServletRequest request, Principal principal, RedirectAttributes flash) {
        try {for (com.example.AgenciaViajes.modelo.carrito.ItemCarrito item : carrito.getItems()) {
                String fechaStr = request.getParameter("fecha_" + item.getClave());
                
                if (fechaStr == null || fechaStr.isEmpty()) {
                    throw new IllegalArgumentException("Debes seleccionar una fecha para el paquete: " + item.getNombrePaquete());
                }
                
                LocalDate fechaViaje = LocalDate.parse(fechaStr);
                if (fechaViaje.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("La fecha de " + item.getNombrePaquete() + " no puede ser anterior a hoy.");
                }
                
                item.setFechaViaje(fechaViaje);
            }

            reservaService.crearDesdeCarrito(carrito, principal.getName());
            flash.addFlashAttribute("exito", "¡Reserva creada! Quedó pendiente de confirmación por la agencia.");
            return "redirect:/reservas/mis-reservas";
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }
}