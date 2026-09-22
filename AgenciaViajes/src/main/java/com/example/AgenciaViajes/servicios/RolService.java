package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Rol;
import com.example.AgenciaViajes.repositorio.RolRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Rol buscarPorId(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + id));
    }

    public long contar() {
        return rolRepository.count();
    }

    @Transactional
    public Rol crear(String nombreRol) {
        String nombre = normalizar(nombreRol);
        if (rolRepository.findByNombreRol(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre '" + nombre + "'.");
        }
        Rol rol = new Rol();
        rol.setNombreRol(nombre);
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol actualizar(Integer id, String nombreRol) {
        Rol rol = buscarPorId(id);
        String nombre = normalizar(nombreRol);

        rolRepository.findByNombreRol(nombre).ifPresent(existente -> {
            if (!existente.getIdRol().equals(id)) {
                throw new IllegalArgumentException("Ya existe un rol con el nombre '" + nombre + "'.");
            }
        });

        rol.setNombreRol(nombre);
        return rolRepository.save(rol);
    }

    /** Falla con un mensaje claro si el rol todavía tiene usuarios asignados (restricción de FK). */
    @Transactional
    public void eliminar(Integer id) {
        Rol rol = buscarPorId(id);
        try {
            rolRepository.delete(rol);
            rolRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el rol '" + rol.getNombreRol() + "' porque tiene usuarios asignados.");
        }
    }

    private String normalizar(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }
        return nombreRol.trim().toUpperCase();
    }
}