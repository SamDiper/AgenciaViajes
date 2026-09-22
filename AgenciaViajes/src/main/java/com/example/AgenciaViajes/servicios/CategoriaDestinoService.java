package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.CategoriaDestino;
import com.example.AgenciaViajes.repositorio.CategoriaDestinoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CategoriaDestino buscarPorId(Long id) {
        return categoriaDestinoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
    }

    public long contar() {
        return categoriaDestinoRepository.count();
    }

    @Transactional
    public CategoriaDestino crear(String nombre) {
        String limpio = validarNombre(nombre);
        categoriaDestinoRepository.findAll().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(limpio))
                .findFirst()
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Ya existe la categoría '" + limpio + "'.");
                });

        CategoriaDestino categoria = new CategoriaDestino();
        categoria.setNombre(limpio);
        return categoriaDestinoRepository.save(categoria);
    }

    @Transactional
    public CategoriaDestino actualizar(Long id, String nombre) {
        CategoriaDestino categoria = buscarPorId(id);
        categoria.setNombre(validarNombre(nombre));
        return categoriaDestinoRepository.save(categoria);
    }

    

    /** Falla con un mensaje claro si hay destinos que usan esta categoría (restricción de FK). */
    @Transactional
    public void eliminar(Long id) {
        CategoriaDestino categoria = buscarPorId(id);
        try {
            categoriaDestinoRepository.delete(categoria);
            categoriaDestinoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar '" + categoria.getNombre() + "' porque hay destinos que la usan.");
        }
    }

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        return nombre.trim();
    }
}