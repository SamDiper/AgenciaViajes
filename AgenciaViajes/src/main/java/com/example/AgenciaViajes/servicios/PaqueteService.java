package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.repositorio.PaqueteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaqueteService {

    @Autowired
    private PaqueteRepository paqueteRepository;

    public List<Paquete> listarActivos() {
        return paqueteRepository.findByEstado(EstadoGeneral.ACTIVO);
    }

    public List<Paquete> buscar(String destino, BigDecimal precioMin, BigDecimal precioMax,
                                Integer durMin, Integer durMax) {

        return listarActivos().stream()
                .filter(p -> destino == null || destino.isBlank()
                        || p.getDestino().getNombre().toLowerCase().contains(destino.toLowerCase()))
                .filter(p -> precioMin == null || p.getPrecioBase().compareTo(precioMin) >= 0)
                .filter(p -> precioMax == null || p.getPrecioBase().compareTo(precioMax) <= 0)
                .filter(p -> durMin == null || p.getDuracionDias() >= durMin)
                .filter(p -> durMax == null || p.getDuracionDias() <= durMax)
                .collect(Collectors.toList());
    }
}
