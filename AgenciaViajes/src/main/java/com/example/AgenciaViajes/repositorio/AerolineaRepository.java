package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Aerolinea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AerolineaRepository extends JpaRepository<Aerolinea, Long> {

    @Query("SELECT DISTINCT a FROM Aerolinea a JOIN a.destinos d WHERE d.idDestino = :idDestino")
    List<Aerolinea> findByDestinoId(@Param("idDestino") Long idDestino);
}
