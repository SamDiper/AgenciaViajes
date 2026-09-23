package com.example.AgenciaViajes.servicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.DetalleReserva;
import com.example.AgenciaViajes.modelo.Factura;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.modelo.carrito.Carrito;
import com.example.AgenciaViajes.modelo.carrito.ItemCarrito;
import com.example.AgenciaViajes.repositorio.AerolineaRepository;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import com.example.AgenciaViajes.repositorio.HotelRepository;
import com.example.AgenciaViajes.repositorio.PaqueteRepository;
import com.example.AgenciaViajes.repositorio.ReservaRepository;
import com.example.AgenciaViajes.repositorio.UsuarioRepository;
import com.example.AgenciaViajes.servicios.Email.EmailServicio;
import com.example.AgenciaViajes.servicios.Pago.FacturaService;
import com.example.AgenciaViajes.servicios.Pdf.PdfService;  

@Service
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final PaqueteRepository paqueteRepo;
    private final ClienteRepository clienteRepo;
    private final UsuarioRepository usuarioRepo;
    private final AerolineaRepository aerolineaRepo;
    private final HotelRepository hotelRepo;
    private final EmailServicio emailServicio;
    private final PdfService pdfService;
    private final FacturaService facturaService;

    public ReservaService(ReservaRepository reservaRepo,
                          PaqueteRepository paqueteRepo,
                          ClienteRepository clienteRepo,
                          UsuarioRepository usuarioRepo,
                          AerolineaRepository aerolineaRepo,
                          HotelRepository hotelRepo,
                          EmailServicio emailServicio,
                          PdfService pdfService,
                          FacturaService facturaService) {
        this.reservaRepo = reservaRepo;
        this.paqueteRepo = paqueteRepo;
        this.clienteRepo = clienteRepo;
        this.usuarioRepo = usuarioRepo;
        this.aerolineaRepo = aerolineaRepo;
        this.hotelRepo = hotelRepo;
        this.emailServicio = emailServicio;
        this.pdfService = pdfService;
        this.facturaService = facturaService;
    }

public Reserva crearDesdeCarrito(Carrito carrito, String username) {
    if (carrito.isVacio()) {
        throw new IllegalStateException("El carrito está vacío.");
    }

    Cliente cliente = clienteRepo.findByUsuarioNombreUsuario(username)
            .orElseThrow(() -> new IllegalStateException("Tu usuario no tiene un cliente asociado."));

    Reserva reserva = new Reserva();
    reserva.setCliente(cliente);

    for (ItemCarrito item : carrito.getItems()) {
        validarFechaViaje(item.getFechaViaje());

        Paquete paquete = paqueteRepo.findById(item.getIdPaquete())
                .orElseThrow(() -> new IllegalStateException("El paquete ya no existe."));
        Aerolinea aerolinea = item.getIdAerolinea() == null ? null
                : aerolineaRepo.findById(item.getIdAerolinea()).orElse(null);
        Hotel hotel = item.getIdHotel() == null ? null
                : hotelRepo.findById(item.getIdHotel()).orElse(null);

        BigDecimal precio = ItemCarrito.calcularPrecioUnitario(paquete, aerolinea, hotel);

        DetalleReserva detalle = new DetalleReserva();
        detalle.setPaquete(paquete);
        detalle.setAerolinea(aerolinea);
        detalle.setHotel(hotel);
        detalle.setCantidadPersonas(item.getPersonas());
        detalle.setPrecioUnitario(precio);
        detalle.setSubtotal(precio.multiply(BigDecimal.valueOf(item.getPersonas())));
        detalle.setFechaViaje(item.getFechaViaje());
        detalle.setEstado(EstadoReserva.PENDIENTE);

        reserva.addDetalle(detalle);
    }

    reserva.recalcularTotal();
    Reserva guardada = reservaRepo.save(reserva);
    carrito.vaciar();

    return guardada;
}

private void enviarCorreoConfirmacion(Reserva reserva, Cliente cliente, Factura factura) {
    try {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy");

        String destinos = reserva.getDetalles().stream()
                .map(d -> d.getPaquete().getDestino().getNombre())
                .distinct()
                .collect(Collectors.joining(", "));

        LocalDate fechaMasProxima = reserva.getDetalles().stream()
                .map(DetalleReserva::getFechaViaje)
                .min(LocalDate::compareTo)
                .orElse(null);

        String fechaTexto = fechaMasProxima != null
                ? fechaMasProxima.format(formatoFecha)
                : "Por confirmar";

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", cliente.getNombreCompleto());
        variables.put("destino", destinos);
        variables.put("fecha", fechaTexto);
        variables.put("cantidadPaquetes", reserva.getDetalles().size());
        variables.put("mensaje", "¡Tu pago fue recibido y tu reserva #" + reserva.getId()
                + " está confirmada! Adjuntamos tu factura " + factura.getNumero() + " y tu itinerario.");

        String asunto = "Confirmación de Reserva - " + destinos;

        Map<String, byte[]> adjuntos = new LinkedHashMap<>();
        adjuntos.put("factura_" + factura.getNumero() + ".pdf", pdfService.generarFactura(factura));
        adjuntos.put("itinerario_reserva_" + reserva.getId() + ".pdf", pdfService.generarItinerario(reserva));

        emailServicio.enviarCorreoConAdjuntos(
                cliente.getCorreo(),
                asunto,
                "Email/correo",
                variables,
                adjuntos
        );
    } catch (Exception e) {
        System.err.println("No se pudo enviar el correo de confirmación: " + e.getMessage());
    }
}


    public Reserva cancelarDetalle(Integer idReserva, Integer idDetalle, String username) {
        Reserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));
                
        if (!esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta reserva.");
        }

        DetalleReserva detalle = reserva.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El paquete no pertenece a esta reserva."));

        if (detalle.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden cancelar paquetes pendientes.");
        }

        detalle.setEstado(EstadoReserva.CANCELADA);
        reserva.recalcularTotal(); // Ajusta el costo tras la cancelación
        
        boolean todosCancelados = reserva.getDetalles().stream()
                .allMatch(d -> d.getEstado() == EstadoReserva.CANCELADA);
        if (todosCancelados) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        }

        return reservaRepo.save(reserva);
    }

    public List<Reserva> buscar(Integer idCliente, Long idDestino, EstadoReserva estado,
                                LocalDate desde, LocalDate hasta) {
        return reservaRepo.buscar(idCliente, idDestino, estado, desde, hasta);
    }


    public Reserva actualizarFechaViajeDetalle(Integer idReserva, Integer idDetalle, LocalDate nuevaFecha, String username, boolean esStaff) {
        Reserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        if (!esStaff && !esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta reserva.");
        }

        DetalleReserva detalle = reserva.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El paquete no pertenece a esta reserva."));

        if (detalle.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se puede modificar la fecha de un paquete pendiente.");
        }

        if (nuevaFecha == null || nuevaFecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de viaje no puede ser nula ni anterior a hoy.");
        }

        detalle.setFechaViaje(nuevaFecha);
        return reservaRepo.save(reserva);
    }

    public List<Reserva> misReservas(String username) {
    Cliente cliente = clienteRepo.findByUsuarioNombreUsuario(username).orElse(null);
    if (cliente == null) {
        return List.of();
    }
    return reservaRepo.findByClienteIdClienteOrderByFechaReservaDesc(cliente.getIdCliente());
}

    public Reserva obtener(Integer id, String username, boolean esStaff) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));
        if (!esStaff && !esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para ver esta reserva.");
        }
        return reserva;
    }

    public Reserva pagarReserva(Integer id, String username, boolean esStaff) {
        return pagarReserva(id, username, esStaff, "Pago simulado", null);
    }

    @Transactional
    public Reserva pagarReserva(Integer id, String username, boolean esStaff,
                                String metodoPago, String ultimosDigitos) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        if (!esStaff && !esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para pagar esta reserva.");
        }
        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden pagar reservas que estén en estado pendiente.");
        }

        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);

        for (DetalleReserva detalle : reserva.getDetalles()) {
            if (detalle.getEstado() == EstadoReserva.PENDIENTE) {
                detalle.setEstado(EstadoReserva.CONFIRMADA);
            }
        }

        usuarioRepo.findByNombreUsuario(username).ifPresent(reserva::setUsuarioGestiona);
        Reserva pagada = reservaRepo.save(reserva);

        Factura factura = facturaService.generar(pagada, metodoPago, ultimosDigitos);

        enviarCorreoConfirmacion(pagada, pagada.getCliente(), factura);

        return pagada;
    }


    public Reserva cambiarEstado(Integer id, EstadoReserva nuevoEstado, String username) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        validarTransicion(reserva.getEstadoReserva(), nuevoEstado);

        reserva.setEstadoReserva(nuevoEstado);
        usuarioRepo.findByNombreUsuario(username).ifPresent(reserva::setUsuarioGestiona);
        return reservaRepo.save(reserva);
    }


    public Reserva cancelarPropia(Integer id, String username) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        if (!esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta reserva.");
        }
        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden cancelar reservas pendientes.");
        }
        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        return reservaRepo.save(reserva);
    }


    public Reserva actualizarFechaViaje(Integer id, LocalDate nuevaFecha, String username, boolean esStaff) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        if (!esStaff && !esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta reserva.");
        }
        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se puede modificar una reserva pendiente.");
        }
        validarFechaViaje(nuevaFecha);

        reserva.setFechaViaje(nuevaFecha);
        return reservaRepo.save(reserva);
    }


    private void validarFechaViaje(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debes indicar la fecha de viaje.");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de viaje no puede ser anterior a hoy.");
        }
    }

    private void validarTransicion(EstadoReserva actual, EstadoReserva nuevo) {
        if (actual == nuevo) {
            throw new IllegalStateException("La reserva ya está en estado " + actual + ".");
        }
        if (actual == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("Una reserva cancelada no puede modificarse.");
        }
        if (actual == EstadoReserva.CONFIRMADA && nuevo == EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Una reserva confirmada no puede volver a pendiente.");
        }
    }

    private boolean esDuenio(Reserva reserva, String username) {
        return reserva.getCliente().getUsuario() != null
                && username.equals(reserva.getCliente().getUsuario().getNombreUsuario());
    }

    public List<Reserva> listarTodasOrdenadasPorFecha() {
        return reservaRepo.findAllByOrderByFechaReservaDesc();
    }

    public long contar() {
        return reservaRepo.count();
    }
}