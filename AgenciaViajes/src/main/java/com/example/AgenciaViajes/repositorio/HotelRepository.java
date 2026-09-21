package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByEstado(EstadoGeneral estado);

    List<Hotel> findByDestinoAndEstado(Destino destino, EstadoGeneral estado);

    @Query("SELECT h FROM Hotel h WHERE h.destino.idDestino = :idDestino AND h.estado = :estado")
    List<Hotel> findByDestinoIdAndEstado(@Param("idDestino") Long idDestino, @Param("estado") EstadoGeneral estado);
}
