package com.example.AgenciaViajes.controllers;

import com.example.AgenciaViajes.modelo.Factura;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.servicios.Pago.FacturaService;
import com.example.AgenciaViajes.servicios.ReservaService;
import com.example.AgenciaViajes.servicios.Pdf.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/pago")
public class PagoController {

    private final ReservaService reservaService;
    private final FacturaService facturaService;
    private final PdfService pdfService;

    public PagoController(ReservaService reservaService,
                          FacturaService facturaService,
                          PdfService pdfService) {
        this.reservaService = reservaService;
        this.facturaService = facturaService;
        this.pdfService = pdfService;
    }

    @GetMapping("/{idReserva}")
    public String pasarela(@PathVariable Integer idReserva, Principal principal, Model model) {
        Reserva reserva = reservaService.obtener(idReserva, principal.getName(), false);

        if (reserva.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
            return "redirect:/pago/" + idReserva + "/exito";
        }

        model.addAttribute("reserva", reserva);
        return "auth/pago/pasarela";
    }

    @PostMapping("/{idReserva}")
    public String pagar(@PathVariable Integer idReserva,
                        @RequestParam String titular,
                        @RequestParam String numeroTarjeta,
                        @RequestParam String vencimiento,
                        @RequestParam String cvv,
                        @RequestParam(defaultValue = "Tarjeta de crédito") String tipoTarjeta,
                        Principal principal,
                        RedirectAttributes ra) {

        String numero = numeroTarjeta.replaceAll("[\\s-]", "");

        if (titular.isBlank()) {
            ra.addFlashAttribute("error", "Escribe el nombre del titular de la tarjeta.");
            return "redirect:/pago/" + idReserva;
        }
        if (!numero.matches("\\d{16}")) {
            ra.addFlashAttribute("error", "El número de tarjeta debe tener 16 dígitos.");
            return "redirect:/pago/" + idReserva;
        }
        if (!vencimiento.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            ra.addFlashAttribute("error", "La fecha de vencimiento debe tener el formato MM/AA.");
            return "redirect:/pago/" + idReserva;
        }
        if (!cvv.matches("\\d{3,4}")) {
            ra.addFlashAttribute("error", "El CVV debe tener 3 o 4 dígitos.");
            return "redirect:/pago/" + idReserva;
        }

        if (numero.endsWith("0000")) {
            ra.addFlashAttribute("error", "Pago rechazado por el banco. Intenta con otra tarjeta.");
            return "redirect:/pago/" + idReserva;
        }

        try {
            String ultimos4 = numero.substring(12);
            reservaService.pagarReserva(idReserva, principal.getName(), false, tipoTarjeta, ultimos4);
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pago/" + idReserva;
        }

        return "redirect:/pago/" + idReserva + "/exito";
    }

    @GetMapping("/{idReserva}/exito")
    public String exito(@PathVariable Integer idReserva, Principal principal, Model model) {
        Reserva reserva = reservaService.obtener(idReserva, principal.getName(), false);
        Factura factura = facturaService.buscarPorReserva(idReserva).orElse(null);

        if (factura == null) {
            return "redirect:/pago/" + idReserva;
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("factura", factura);
        return "auth/pago/exito";
    }

    @GetMapping("/{idReserva}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Integer idReserva, Principal principal) {
        reservaService.obtener(idReserva, principal.getName(), false); // valida que sea el dueño
        Factura factura = facturaService.buscarPorReserva(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Esta reserva no tiene factura."));

        byte[] pdf = pdfService.generarFactura(factura);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=factura_" + factura.getNumero() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}