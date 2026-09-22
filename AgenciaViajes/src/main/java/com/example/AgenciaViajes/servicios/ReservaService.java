package com.example.AgenciaViajes.servicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.DetalleReserva;
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

    public ReservaService(ReservaRepository reservaRepo,
                          PaqueteRepository paqueteRepo,
                          ClienteRepository clienteRepo,
                          UsuarioRepository usuarioRepo,
                          AerolineaRepository aerolineaRepo,
                          HotelRepository hotelRepo,
                          EmailServicio emailServicio,
                          PdfService pdfService) {
        this.reservaRepo = reservaRepo;
        this.paqueteRepo = paqueteRepo;
        this.clienteRepo = clienteRepo;
        this.usuarioRepo = usuarioRepo;
        this.aerolineaRepo = aerolineaRepo;
        this.hotelRepo = hotelRepo;
        this.emailServicio = emailServicio;
        this.pdfService = pdfService;
    }

    // ------------------------------------------------------------------
    // Crear
    // ------------------------------------------------------------------

    @Transactional
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

    enviarCorreoConfirmacion(guardada, cliente);

    return guardada;
}

private void enviarCorreoConfirmacion(Reserva reserva, Cliente cliente) {
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
        variables.put("mensaje", "¡Tu reserva #" + reserva.getId() + " ha sido registrada exitosamente!");

        String asunto = "Confirmación de Reserva - " + destinos;

        byte[] pdfItinerario = pdfService.generarItinerario(reserva);
        String nombreAdjunto = "itinerario_reserva_" + reserva.getId() + ".pdf";

        emailServicio.enviarCorreoReserva(
                cliente.getCorreo(),
                asunto,
                "Email/correo",
                variables,
                pdfItinerario,
                nombreAdjunto
        );
    } catch (Exception e) {
        System.err.println("No se pudo enviar el correo de confirmación: " + e.getMessage());
    }
}

    @Transactional
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
        
        // Si todos los detalles están cancelados, cancelamos toda la reserva por lógica de negocio
        boolean todosCancelados = reserva.getDetalles().stream()
                .allMatch(d -> d.getEstado() == EstadoReserva.CANCELADA);
        if (todosCancelados) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        }

        return reservaRepo.save(reserva);
    }
    // ------------------------------------------------------------------
    // Consultar
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Reserva> buscar(Integer idCliente, Long idDestino, EstadoReserva estado,
                                LocalDate desde, LocalDate hasta) {
        return reservaRepo.buscar(idCliente, idDestino, estado, desde, hasta);
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public List<Reserva> misReservas(String username) {
        Cliente cliente = clienteRepo.findByUsuarioNombreUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Tu usuario no tiene un cliente asociado."));
        return reservaRepo.findByClienteIdClienteOrderByFechaReservaDesc(cliente.getIdCliente());
    }

    /**
     * Devuelve la reserva si el usuario es staff o es el dueño.
     * Evita que un cliente vea reservas ajenas cambiando el id en la URL.
     */
    @Transactional(readOnly = true)
    public Reserva obtener(Integer id, String username, boolean esStaff) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));
        if (!esStaff && !esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para ver esta reserva.");
        }
        return reserva;
    }

    // ------------------------------------------------------------------
    // Modificar
    // ------------------------------------------------------------------
    // Modificar
    // ------------------------------------------------------------------

    /** Pagar / simulación de pago de reserva (cliente o staff). */
    @Transactional
    public Reserva pagarReserva(Integer id, String username, boolean esStaff) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        if (!esStaff && !esDuenio(reserva, username)) {
            throw new AccessDeniedException("No tienes permiso para pagar esta reserva.");
        }
        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden pagar reservas que estén en estado pendiente.");
        }

        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        usuarioRepo.findByNombreUsuario(username).ifPresent(reserva::setUsuarioGestiona);
        return reservaRepo.save(reserva);
    }

    /** Cambio de estado hecho por ADMIN o EMPLEADO. */
    @Transactional
    public Reserva cambiarEstado(Integer id, EstadoReserva nuevoEstado, String username) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe."));

        validarTransicion(reserva.getEstadoReserva(), nuevoEstado);

        reserva.setEstadoReserva(nuevoEstado);
        usuarioRepo.findByNombreUsuario(username).ifPresent(reserva::setUsuarioGestiona);
        return reservaRepo.save(reserva);
    }

    /** Un cliente solo puede cancelar sus propias reservas pendientes. */
    @Transactional
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

    /** Cambio de fecha de viaje (solo mientras esté pendiente). */
    @Transactional
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

    // ------------------------------------------------------------------
    // Reglas internas
    // ------------------------------------------------------------------

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
}