package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO;
import com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO.*;
import com.example.AgenciaViajes.modelo.*;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.repositorio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final DestinoRepository destinoRepository;
    private final PaqueteRepository paqueteRepository;
    private final CategoriaDestinoRepository categoriaDestinoRepository;

    public DashboardService(ReservaRepository reservaRepository,
                            ClienteRepository clienteRepository,
                            DestinoRepository destinoRepository,
                            PaqueteRepository paqueteRepository,
                            CategoriaDestinoRepository categoriaDestinoRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.destinoRepository = destinoRepository;
        this.paqueteRepository = paqueteRepository;
        this.categoriaDestinoRepository = categoriaDestinoRepository;
    }

    public DashboardStatsDTO obtenerEstadisticas(LocalDate fechaInicio, LocalDate fechaFin, Long idCategoria) {
        List<Reserva> todasLasReservas = reservaRepository.findAllByOrderByFechaReservaDesc();

        List<Reserva> reservasFiltradas = todasLasReservas.stream().filter(r -> {
            LocalDate fechaReserva = r.getFechaReserva() != null ? r.getFechaReserva().toLocalDate() : null;

            if (fechaInicio != null && fechaReserva != null && fechaReserva.isBefore(fechaInicio)) {
                return false;
            }
            if (fechaFin != null && fechaReserva != null && fechaReserva.isAfter(fechaFin)) {
                return false;
            }

            if (idCategoria != null && idCategoria > 0) {
                boolean coincideCategoria = r.getDetalles().stream().anyMatch(d ->
                        d.getPaquete() != null &&
                        d.getPaquete().getDestino() != null &&
                        d.getPaquete().getDestino().getCategoria() != null &&
                        idCategoria.equals(d.getPaquete().getDestino().getCategoria().getIdCategoria())
                );
                if (!coincideCategoria) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());

        long totalReservas = reservasFiltradas.size();
        long totalConfirmadas = reservasFiltradas.stream().filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA).count();
        long totalPendientes = reservasFiltradas.stream().filter(r -> r.getEstadoReserva() == EstadoReserva.PENDIENTE).count();
        long totalCanceladas = reservasFiltradas.stream().filter(r -> r.getEstadoReserva() == EstadoReserva.CANCELADA).count();

        BigDecimal totalIngresos = reservasFiltradas.stream()
                .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA && r.getTotal() != null)
                .map(Reserva::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Set<Integer> clientesIds = reservasFiltradas.stream()
                .filter(r -> r.getCliente() != null && r.getCliente().getIdCliente() != null)
                .map(r -> r.getCliente().getIdCliente())
                .collect(Collectors.toSet());
        long totalClientes = clientesIds.size();

        Map<Long, List<DetalleReserva>> detallesPorDestino = new HashMap<>();
        for (Reserva r : reservasFiltradas) {
            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) continue;
            for (DetalleReserva d : r.getDetalles()) {
                if (d.getEstado() == EstadoReserva.CANCELADA) continue;
                if (d.getPaquete() != null && d.getPaquete().getDestino() != null) {
                    Long idDestino = d.getPaquete().getDestino().getIdDestino();
                    if (idCategoria == null || idCategoria <= 0 ||
                            (d.getPaquete().getDestino().getCategoria() != null &&
                             idCategoria.equals(d.getPaquete().getDestino().getCategoria().getIdCategoria()))) {
                        detallesPorDestino.computeIfAbsent(idDestino, k -> new ArrayList<>()).add(d);
                    }
                }
            }
        }

        List<VentaDestinoDTO> ventasPorDestino = new ArrayList<>();
        BigDecimal totalIngresosDestinos = BigDecimal.ZERO;

        for (Map.Entry<Long, List<DetalleReserva>> entry : detallesPorDestino.entrySet()) {
            List<DetalleReserva> list = entry.getValue();
            if (list.isEmpty()) continue;

            Destino dest = list.get(0).getPaquete().getDestino();
            long reservasCount = list.stream().map(DetalleReserva::getReserva).map(Reserva::getId).distinct().count();
            BigDecimal subtotalSum = list.stream()
                    .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalIngresosDestinos = totalIngresosDestinos.add(subtotalSum);

            ventasPorDestino.add(VentaDestinoDTO.builder()
                    .idDestino(dest.getIdDestino())
                    .nombreDestino(dest.getNombre())
                    .categoria(dest.getCategoria() != null ? dest.getCategoria().getNombre() : "General")
                    .ciudad(dest.getCiudad())
                    .totalReservas(reservasCount)
                    .totalVentas(subtotalSum)
                    .imagenUrl(dest.getImagenUrl())
                    .build());
        }

        final BigDecimal totalDestinosCalc = totalIngresosDestinos.compareTo(BigDecimal.ZERO) > 0 ? totalIngresosDestinos : BigDecimal.ONE;
        ventasPorDestino.forEach(vd -> {
            double pct = vd.getTotalVentas()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalDestinosCalc, 2, RoundingMode.HALF_UP)
                    .doubleValue();
            vd.setPorcentaje(pct);
        });
        ventasPorDestino.sort((a, b) -> b.getTotalVentas().compareTo(a.getTotalVentas()));

        Map<Integer, List<Reserva>> reservasPorCliente = reservasFiltradas.stream()
                .filter(r -> r.getCliente() != null)
                .collect(Collectors.groupingBy(r -> r.getCliente().getIdCliente()));

        List<VentaClienteDTO> clientesFrecuentes = new ArrayList<>();
        for (Map.Entry<Integer, List<Reserva>> entry : reservasPorCliente.entrySet()) {
            List<Reserva> resList = entry.getValue();
            if (resList.isEmpty()) continue;

            Cliente c = resList.get(0).getCliente();
            long conf = resList.stream().filter(r -> r.getEstadoReserva() == EstadoReserva.CONFIRMADA).count();
            long tot = resList.size();
            BigDecimal gastado = resList.stream()
                    .filter(r -> r.getEstadoReserva() != EstadoReserva.CANCELADA && r.getTotal() != null)
                    .map(Reserva::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            LocalDateTime ultimaFecha = resList.stream()
                    .map(Reserva::getFechaReserva)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            clientesFrecuentes.add(VentaClienteDTO.builder()
                    .idCliente(c.getIdCliente())
                    .nombreCliente(c.getNombreCompleto())
                    .documento(c.getDocumento())
                    .correo(c.getCorreo())
                    .telefono(c.getTelefono())
                    .reservasConfirmadas(conf)
                    .reservasTotales(tot)
                    .totalGastado(gastado)
                    .ultimaFechaReserva(ultimaFecha)
                    .build());
        }
        clientesFrecuentes.sort((a, b) -> b.getTotalGastado().compareTo(a.getTotalGastado()));

        Map<Long, List<DetalleReserva>> detallesPorPaquete = new HashMap<>();
        for (Reserva r : reservasFiltradas) {
            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) continue;
            for (DetalleReserva d : r.getDetalles()) {
                if (d.getEstado() == EstadoReserva.CANCELADA) continue;
                if (d.getPaquete() != null) {
                    Long idPaq = d.getPaquete().getIdPaquete();
                    if (idCategoria == null || idCategoria <= 0 ||
                            (d.getPaquete().getDestino() != null &&
                             d.getPaquete().getDestino().getCategoria() != null &&
                             idCategoria.equals(d.getPaquete().getDestino().getCategoria().getIdCategoria()))) {
                        detallesPorPaquete.computeIfAbsent(idPaq, k -> new ArrayList<>()).add(d);
                    }
                }
            }
        }

        List<VentaPaqueteDTO> ventasPorPaquete = new ArrayList<>();
        for (Map.Entry<Long, List<DetalleReserva>> entry : detallesPorPaquete.entrySet()) {
            List<DetalleReserva> list = entry.getValue();
            if (list.isEmpty()) continue;

            Paquete paq = list.get(0).getPaquete();
            long vendida = list.size();
            BigDecimal ingresosPaq = list.stream()
                    .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            ventasPorPaquete.add(VentaPaqueteDTO.builder()
                    .idPaquete(paq.getIdPaquete())
                    .nombrePaquete(paq.getNombre())
                    .nombreDestino(paq.getDestino() != null ? paq.getDestino().getNombre() : "N/A")
                    .categoriaDestino(paq.getDestino() != null && paq.getDestino().getCategoria() != null ?
                            paq.getDestino().getCategoria().getNombre() : "N/A")
                    .precioBase(paq.getPrecioBase())
                    .duracionDias(paq.getDuracionDias())
                    .cantidadVendida(vendida)
                    .totalIngresos(ingresosPaq)
                    .build());
        }
        ventasPorPaquete.sort((a, b) -> b.getTotalIngresos().compareTo(a.getTotalIngresos()));

        Map<String, List<Reserva>> reservasPorPeriodo = new TreeMap<>();
        DateTimeFormatter claveFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "CO"));

        for (Reserva r : reservasFiltradas) {
            if (r.getEstadoReserva() == EstadoReserva.CANCELADA || r.getFechaReserva() == null) continue;
            String key = r.getFechaReserva().format(claveFormatter);
            reservasPorPeriodo.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        List<VentaTemporadaDTO> ingresosPorTemporada = new ArrayList<>();
        for (Map.Entry<String, List<Reserva>> entry : reservasPorPeriodo.entrySet()) {
            String key = entry.getKey();
            List<Reserva> rList = entry.getValue();

            BigDecimal ing = rList.stream()
                    .map(r -> r.getTotal() != null ? r.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            LocalDate dateSample = LocalDate.parse(key + "-01");
            String label = dateSample.format(labelFormatter);
            label = label.substring(0, 1).toUpperCase() + label.substring(1);

            ingresosPorTemporada.add(VentaTemporadaDTO.builder()
                    .periodoClave(key)
                    .etiqueta(label)
                    .ingresos(ing)
                    .cantidadReservas(rList.size())
                    .build());
        }

        Map<String, List<DetalleReserva>> detallesPorCategoria = new HashMap<>();
        for (Reserva r : reservasFiltradas) {
            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) continue;
            for (DetalleReserva d : r.getDetalles()) {
                if (d.getEstado() == EstadoReserva.CANCELADA) continue;
                if (d.getPaquete() != null && d.getPaquete().getDestino() != null && d.getPaquete().getDestino().getCategoria() != null) {
                    String catNombre = d.getPaquete().getDestino().getCategoria().getNombre();
                    detallesPorCategoria.computeIfAbsent(catNombre, k -> new ArrayList<>()).add(d);
                }
            }
        }

        List<CategoriaVentaDTO> distribucionCategorias = new ArrayList<>();
        BigDecimal totalIngresosCategorias = BigDecimal.ZERO;

        for (Map.Entry<String, List<DetalleReserva>> entry : detallesPorCategoria.entrySet()) {
            String catName = entry.getKey();
            List<DetalleReserva> dList = entry.getValue();
            long totalR = dList.stream().map(DetalleReserva::getReserva).map(Reserva::getId).distinct().count();
            BigDecimal catIngresos = dList.stream()
                    .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalIngresosCategorias = totalIngresosCategorias.add(catIngresos);

            distribucionCategorias.add(CategoriaVentaDTO.builder()
                    .nombreCategoria(catName)
                    .totalReservas(totalR)
                    .totalIngresos(catIngresos)
                    .build());
        }

        final BigDecimal catTotalCalc = totalIngresosCategorias.compareTo(BigDecimal.ZERO) > 0 ? totalIngresosCategorias : BigDecimal.ONE;
        distribucionCategorias.forEach(c -> {
            double pct = c.getTotalIngresos()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(catTotalCalc, 2, RoundingMode.HALF_UP)
                    .doubleValue();
            c.setPorcentaje(pct);
        });
        distribucionCategorias.sort((a, b) -> b.getTotalIngresos().compareTo(a.getTotalIngresos()));

        List<TransaccionResumenDTO> ultimasTransacciones = reservasFiltradas.stream()
                .limit(15)
                .map(r -> {
                    String destinos = r.getDetalles().stream()
                            .filter(d -> d.getPaquete() != null && d.getPaquete().getDestino() != null)
                            .map(d -> d.getPaquete().getDestino().getNombre())
                            .distinct()
                            .collect(Collectors.joining(", "));
                    if (destinos.isBlank()) destinos = "Sin destino especificado";

                    String paquetes = r.getDetalles().stream()
                            .filter(d -> d.getPaquete() != null)
                            .map(d -> d.getPaquete().getNombre())
                            .distinct()
                            .collect(Collectors.joining(", "));
                    if (paquetes.isBlank()) paquetes = "General";

                    LocalDate fechaViaje = r.getDetalles().stream()
                            .map(DetalleReserva::getFechaViaje)
                            .filter(Objects::nonNull)
                            .min(LocalDate::compareTo)
                            .orElse(r.getFechaViaje());

                    return TransaccionResumenDTO.builder()
                            .idReserva(r.getId())
                            .cliente(r.getCliente() != null ? r.getCliente().getNombreCompleto() : "Anónimo")
                            .destino(destinos)
                            .paquete(paquetes)
                            .fechaReserva(r.getFechaReserva())
                            .fechaViaje(fechaViaje)
                            .total(r.getTotal() != null ? r.getTotal() : BigDecimal.ZERO)
                            .estado(r.getEstadoReserva() != null ? r.getEstadoReserva().name() : "PENDIENTE")
                            .build();
                }).collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalIngresos(totalIngresos)
                .totalReservas(totalReservas)
                .totalConfirmadas(totalConfirmadas)
                .totalPendientes(totalPendientes)
                .totalCanceladas(totalCanceladas)
                .totalClientes(totalClientes)
                .ventasPorDestino(ventasPorDestino)
                .clientesFrecuentes(clientesFrecuentes)
                .ventasPorPaquete(ventasPorPaquete)
                .ingresosPorTemporada(ingresosPorTemporada)
                .distribucionCategorias(distribucionCategorias)
                .ultimasTransacciones(ultimasTransacciones)
                .build();
    }

    public String obtenerNombreCategoria(Long idCategoria) {
        if (idCategoria == null || idCategoria <= 0) return "Todas";
        return categoriaDestinoRepository.findById(idCategoria)
                .map(CategoriaDestino::getNombre)
                .orElse("Todas");
    }
}
