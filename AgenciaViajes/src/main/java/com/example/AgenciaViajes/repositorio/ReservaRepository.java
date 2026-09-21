package com.example.AgenciaViajes.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByClienteIdOrderByFechaReservaDesc(Integer idCliente);

    @Query("""
            SELECT r FROM Reserva r
            WHERE (:idCliente IS NULL OR r.cliente.id = :idCliente)
              AND (:estado IS NULL OR r.estadoReserva = :estado)
              AND (:desde IS NULL OR r.fechaViaje >= :desde)
              AND (:hasta IS NULL OR r.fechaViaje <= :hasta)
              AND (:idDestino IS NULL OR EXISTS (
                    SELECT d.id FROM DetalleReserva d
                    WHERE d.reserva = r AND d.paquete.destino.id = :idDestino))
            ORDER BY r.fechaReserva DESC
            """)
    List<Reserva> buscar(@Param("idCliente") Integer idCliente,
                         @Param("idDestino") Integer idDestino,
                         @Param("estado") EstadoReserva estado,
                         @Param("desde") LocalDate desde,
                         @Param("hasta") LocalDate hasta);
}
