package com.example.AgenciaViajes.repositorio;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

  List<Reserva> findByClienteIdClienteOrderByFechaReservaDesc(Integer idCliente);

  List<Reserva> findAllByOrderByFechaReservaDesc();

  @Query("""
      SELECT DISTINCT r FROM Reserva r
      LEFT JOIN r.detalles d
      WHERE (:idCliente IS NULL OR r.cliente.idCliente = :idCliente)
        AND (:estado IS NULL OR r.estadoReserva = :estado)
        AND (:desde IS NULL OR d.fechaViaje >= :desde)
        AND (:hasta IS NULL OR d.fechaViaje <= :hasta)
        AND (:idDestino IS NULL OR d.paquete.destino.idDestino = :idDestino)
      ORDER BY r.fechaReserva DESC
      """)
  List<Reserva> buscar(@Param("idCliente") Integer idCliente,
      @Param("idDestino") Long idDestino,
      @Param("estado") EstadoReserva estado,
      @Param("desde") LocalDate desde,
      @Param("hasta") LocalDate hasta);
}