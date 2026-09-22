package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.CategoriaDestino;
import com.example.AgenciaViajes.repositorio.CategoriaDestinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaDestinoService {

    private final CategoriaDestinoRepository categoriaDestinoRepository;

    public CategoriaDestinoService(CategoriaDestinoRepository categoriaDestinoRepository) {
        this.categoriaDestinoRepository = categoriaDestinoRepository;
    }

    public List<CategoriaDestino> listarTodas() {
        return categoriaDestinoRepository.findAll();
    }

    public Optional<CategoriaDestino> obtenerPorId(Long id) {
        return categoriaDestinoRepository.findById(id);
    }

    public long contar() {
        return categoriaDestinoRepository.count();
    }
}
