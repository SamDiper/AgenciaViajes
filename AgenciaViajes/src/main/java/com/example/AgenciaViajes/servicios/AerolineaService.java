package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.repositorio.AerolineaRepository;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AerolineaService {

    private final AerolineaRepository aerolineaRepository;
    private final DestinoRepository destinoRepository;

    public AerolineaService(AerolineaRepository aerolineaRepository, DestinoRepository destinoRepository) {
        this.aerolineaRepository = aerolineaRepository;
        this.destinoRepository = destinoRepository;
    }

    public List<Aerolinea> listarTodas() {
        return aerolineaRepository.findAll();
    }

    public Optional<Aerolinea> obtenerPorId(Long id) {
        return aerolineaRepository.findById(id);
    }

    public long contar() {
        return aerolineaRepository.count();
    }


    public Aerolinea guardar(Aerolinea aerolinea, List<Long> destinoIds) {
        if (destinoIds != null && !destinoIds.isEmpty()) {
            List<Destino> destinos = destinoRepository.findAllById(destinoIds);
            aerolinea.setDestinos(destinos);
        } else {
            aerolinea.setDestinos(new ArrayList<>());
        }
        return aerolineaRepository.save(aerolinea);
    }


    public void eliminar(Long id) {
        aerolineaRepository.deleteById(id);
    }

    public List<Aerolinea> listarPorDestino(Long idDestino) {
        return aerolineaRepository.findByDestinoId(idDestino);
    }
}
