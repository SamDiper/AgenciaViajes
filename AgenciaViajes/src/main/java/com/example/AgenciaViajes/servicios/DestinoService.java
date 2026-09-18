package com.example.AgenciaViajes.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.AgenciaViajes.modelo.Destino;

import com.example.AgenciaViajes.repositorio.DestinoRepository;

@Service
public class DestinoService {

    @Autowired
    private DestinoRepository destinoRepository;

    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    public List<Destino> destacados() {
        return destinoRepository.findTop6ByOrderByIdDestinoDesc();
    }

    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Destino no encontrado: " + id));
    }
}