package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Paquete;
import com.example.AgenciaViajes.repositorio.AerolineaRepository;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import com.example.AgenciaViajes.repositorio.HotelRepository;
import com.example.AgenciaViajes.repositorio.PaqueteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaqueteService {

    private final PaqueteRepository paqueteRepository;
    private final DestinoRepository destinoRepository;
    private final AerolineaRepository aerolineaRepository;
    private final HotelRepository hotelRepository;

    public PaqueteService(PaqueteRepository paqueteRepository,
                          DestinoRepository destinoRepository,
                          AerolineaRepository aerolineaRepository,
                          HotelRepository hotelRepository) {
        this.paqueteRepository = paqueteRepository;
        this.destinoRepository = destinoRepository;
        this.aerolineaRepository = aerolineaRepository;
        this.hotelRepository = hotelRepository;
    }

    public List<Paquete> listarTodos() {
        return paqueteRepository.findAll();
    }

    public List<Paquete> listarActivos() {
        return paqueteRepository.findByEstado(EstadoGeneral.ACTIVO);
    }

    public Optional<Paquete> obtenerPorId(Long id) {
        return paqueteRepository.findById(id);
    }

    public long contar() {
        return paqueteRepository.count();
    }


    public Paquete guardar(Paquete paquete, List<String> incluye, List<String> itinerario) {
        if (paquete.getDestino() != null && paquete.getDestino().getIdDestino() != null) {
            destinoRepository.findById(paquete.getDestino().getIdDestino()).ifPresent(paquete::setDestino);
        }

        if (paquete.getAerolinea() != null && paquete.getAerolinea().getIdAerolinea() != null) {
            aerolineaRepository.findById(paquete.getAerolinea().getIdAerolinea())
                    .ifPresentOrElse(paquete::setAerolinea, () -> paquete.setAerolinea(null));
        } else {
            paquete.setAerolinea(null);
        }

        if (paquete.getHotel() != null && paquete.getHotel().getIdHotel() != null) {
            hotelRepository.findById(paquete.getHotel().getIdHotel())
                    .ifPresentOrElse(paquete::setHotel, () -> paquete.setHotel(null));
        } else {
            paquete.setHotel(null);
        }

        if (incluye != null) {
            paquete.setIncluye(incluye.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.toList()));
        } else {
            paquete.setIncluye(new ArrayList<>());
        }

        if (itinerario != null) {
            paquete.setItinerario(itinerario.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.toList()));
        } else {
            paquete.setItinerario(new ArrayList<>());
        }

        if (paquete.getIdPaquete() != null) {
            paqueteRepository.findById(paquete.getIdPaquete()).ifPresent(existente -> {
                paquete.setFechaRegistro(existente.getFechaRegistro());
            });
        }

        return paqueteRepository.save(paquete);
    }


    public void eliminar(Long id) {
        paqueteRepository.deleteById(id);
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

