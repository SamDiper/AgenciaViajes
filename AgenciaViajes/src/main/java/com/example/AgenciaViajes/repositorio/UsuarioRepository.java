package com.example.AgenciaViajes.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AgenciaViajes.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
