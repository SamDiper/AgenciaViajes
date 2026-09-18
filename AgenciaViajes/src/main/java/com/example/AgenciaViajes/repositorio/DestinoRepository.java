package com.example.AgenciaViajes.repositorio;

import com.example.AgenciaViajes.modelo.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DestinoRepository extends JpaRepository<Destino, Long> {
    List<Destino> findTop6ByOrderByIdDestinoDesc();
}
