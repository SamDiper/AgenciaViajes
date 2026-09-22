package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Rol;
import com.example.AgenciaViajes.repositorio.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> obtenerPorId(Integer id) {
        return rolRepository.findById(id);
    }

    public long contar() {
        return rolRepository.count();
    }
}
