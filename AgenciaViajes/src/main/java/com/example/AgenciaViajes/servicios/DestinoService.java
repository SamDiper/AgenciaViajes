package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;

    public DestinoService(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    public List<Destino> destacados() {
        return destinoRepository.findTop6ByOrderByIdDestinoDesc();
    }

    public Optional<Destino> obtenerPorId(Long id) {
        return destinoRepository.findById(id);
    }

    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado: " + id));
    }

    public List<Destino> buscarPorIds(List<Long> ids) {
        return destinoRepository.findAllById(ids);
    }

    public long contar() {
        return destinoRepository.count();
    }
}