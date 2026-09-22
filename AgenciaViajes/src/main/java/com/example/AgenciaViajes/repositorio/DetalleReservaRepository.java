package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.DetalleReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Integer> {

    @Query("SELECT d FROM DetalleReserva d JOIN FETCH d.reserva r JOIN FETCH d.paquete p JOIN FETCH p.destino dest")
    List<DetalleReserva> findAllWithRelations();
}
