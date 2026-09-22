package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.Reserva;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDocumento(String documento);
    Optional<Cliente> findByCorreo(String correo);
    Optional<Cliente> findByUsuarioNombreUsuario(String nombreUsuario);
    Optional<Cliente> findByUsuario_IdUsuario(Integer idUsuario);

}
