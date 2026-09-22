package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import com.example.AgenciaViajes.repositorio.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final DestinoRepository destinoRepository;

    public HotelService(HotelRepository hotelRepository, DestinoRepository destinoRepository) {
        this.hotelRepository = hotelRepository;
        this.destinoRepository = destinoRepository;
    }

    public List<Hotel> listarTodos() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> obtenerPorId(Long id) {
        return hotelRepository.findById(id);
    }

    public long contar() {
        return hotelRepository.count();
    }


    public Hotel guardar(Hotel hotel) {
        if (hotel.getDestino() != null && hotel.getDestino().getIdDestino() != null) {
            destinoRepository.findById(hotel.getDestino().getIdDestino()).ifPresent(hotel::setDestino);
        }
        return hotelRepository.save(hotel);
    }


    public void eliminar(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<Hotel> listarPorDestinoYEstado(Long idDestino, EstadoGeneral estado) {
        return hotelRepository.findByDestinoIdAndEstado(idDestino, estado);
    }
}
