package com.example.AgenciaViajes.controllers.api;

import com.example.AgenciaViajes.modelo.Destino;
import com.example.AgenciaViajes.repositorio.DestinoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinos")
@CrossOrigin(origins = "*")
public class DestinoRestController {

    private final DestinoRepository destinoRepository;

    public DestinoRestController(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Destino>> getDestinos() {
        return ResponseEntity.ok(destinoRepository.findAll());
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<Destino>> getDestacados() {
        return ResponseEntity.ok(destinoRepository.findTop6ByOrderByIdDestinoDesc());
    }
}
