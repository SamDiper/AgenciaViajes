package com.example.AgenciaViajes.controllers;

import java.time.LocalDate;
 
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.servicios.ReservaService;
 
@Controller
@RequestMapping("/reservas")
public class ReservaController {
 
    private final ReservaService reservaService;
 
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
 
    // ---------------- Staff: listado con filtros ----------------
 
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public String listar() {
        return "redirect:/admin/reservas";
    }
 
    // ---------------- Cliente: sus reservas ----------------
 
    @GetMapping("/mis-reservas")
    public String misReservas(Authentication auth, Model model) {
        model.addAttribute("reservas", reservaService.misReservas(auth.getName()));
        return "auth/reservas/reserva";
    }
 
    // ---------------- Detalle (staff o dueño) ----------------
 
    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Authentication auth, Model model) {
        boolean staff = esStaff(auth);
        model.addAttribute("reserva", reservaService.obtener(id, auth.getName(), staff));
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("esStaff", staff);
        model.addAttribute("hoy", LocalDate.now());
        return "auth/reservas/detallereserva";
    }

    @PostMapping("/{idReserva}/cancelar-paquete/{idDetalle}")
    public String cancelarDetalle(@PathVariable Integer idReserva, 
                                  @PathVariable Integer idDetalle, 
                                  Authentication auth, 
                                  RedirectAttributes flash) {
        try {
            reservaService.cancelarDetalle(idReserva, idDetalle, auth.getName());
            flash.addFlashAttribute("exito", "Paquete cancelado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/" + idReserva;
    }
 
    @PostMapping("/{idReserva}/fecha/{idDetalle}")
    public String cambiarFechaDetalle(@PathVariable Integer idReserva,
                                      @PathVariable Integer idDetalle,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaViaje,
                                      Authentication auth,
                                      RedirectAttributes flash) {
        try {
            reservaService.actualizarFechaViajeDetalle(idReserva, idDetalle, fechaViaje, auth.getName(), esStaff(auth));
            flash.addFlashAttribute("exito", "Fecha de viaje actualizada para este paquete.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/" + idReserva;
    }
    // ---------------- Acciones ----------------
 
    @PostMapping("/{id}/pagar")
    public String pagar(@PathVariable Integer id, Authentication auth, RedirectAttributes flash) {
        try {
            reservaService.pagarReserva(id, auth.getName(), esStaff(auth));
            flash.addFlashAttribute("exito", "¡Pago procesado exitosamente! Tu reserva ha sido confirmada.");
        } catch (IllegalArgumentException | IllegalStateException | org.springframework.security.access.AccessDeniedException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/" + id;
    }
 
    @PostMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public String cambiarEstado(@PathVariable Integer id,
                                @RequestParam EstadoReserva estado,
                                Authentication auth,
                                RedirectAttributes flash) {
        try {
            reservaService.cambiarEstado(id, estado, auth.getName());
            flash.addFlashAttribute("exito", "Estado actualizado a " + estado + ".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/" + id;
    }
 
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id, Authentication auth, RedirectAttributes flash) {
        try {
            reservaService.cancelarPropia(id, auth.getName());
            flash.addFlashAttribute("exito", "Reserva cancelada.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/" + id;
    }
 
    @PostMapping("/{id}/fecha")
    public String cambiarFecha(@PathVariable Integer id,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaViaje,
                               Authentication auth,
                               RedirectAttributes flash) {
        try {
            reservaService.actualizarFechaViaje(id, fechaViaje, auth.getName(), esStaff(auth));
            flash.addFlashAttribute("exito", "Fecha de viaje actualizada.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/" + id;
    }
 
    // ---------------- Helpers ----------------
 
    private boolean esStaff(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_EMPLEADO"));
    }
}