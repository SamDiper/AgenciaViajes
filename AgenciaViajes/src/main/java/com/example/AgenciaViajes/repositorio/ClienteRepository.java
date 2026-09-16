package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDocumento(String documento);
    Optional<Cliente> findByCorreo(String correo);
    Optional<Cliente> findByUsuario_IdUsuario(Integer idUsuario);
}
