package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Rol;
import  java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Método para buscar el rol por su nombre (ej: "CLIENTE", "ADMIN")
    Optional<Rol> findByNombreRol(String nombreRol);
}
