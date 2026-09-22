package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    Optional<Factura> findByReservaId(Integer idReserva);

    List<Factura> findAllByOrderByFechaEmisionDesc();
}
