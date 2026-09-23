package com.example.AgenciaViajes.servicios.Pago;

import com.example.AgenciaViajes.modelo.Factura;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.repositorio.FacturaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private static final BigDecimal TASA_IVA = new BigDecimal("0.19");

    private final FacturaRepository facturaRepo;

    public FacturaService(FacturaRepository facturaRepo) {
        this.facturaRepo = facturaRepo;
    }

    public Factura generar(Reserva reserva, String metodoPago, String ultimosDigitos) {
        Optional<Factura> existente = facturaRepo.findByReservaId(reserva.getId());
        if (existente.isPresent()) {
            return existente.get();
        }

        BigDecimal total = reserva.getTotal() != null ? reserva.getTotal() : BigDecimal.ZERO;
        BigDecimal subtotal = total.divide(BigDecimal.ONE.add(TASA_IVA), 0, RoundingMode.HALF_UP);
        BigDecimal iva = total.subtract(subtotal);

        Factura factura = new Factura();
        factura.setReserva(reserva);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotal(total);
        factura.setMetodoPago(metodoPago);
        factura.setUltimosDigitos(ultimosDigitos);

        Factura guardada = facturaRepo.save(factura);
        guardada.setNumero(String.format("FAC-%06d", guardada.getId()));
        return facturaRepo.save(guardada);
    }

    public Optional<Factura> buscarPorReserva(Integer idReserva) {
        return facturaRepo.findByReservaId(idReserva);
    }

    public Factura buscarPorId(Integer id) {
        return facturaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La factura no existe."));
    }

    public List<Factura> listarTodas() {
        return facturaRepo.findAllByOrderByFechaEmisionDesc();
    }

    public long contar() {
        return facturaRepo.count();
    }
}
