package com.example.AgenciaViajes.servicios;


import com.example.AgenciaViajes.dto.RegistroClienteDTO;
import com.example.AgenciaViajes.modelo.*;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Enum.TipoDocumento;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import com.example.AgenciaViajes.repositorio.RolRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Cliente registrarCliente(RegistroClienteDTO dto) {
    Rol rolCliente = rolRepository.findById(2)
            .orElseThrow(() -> new IllegalStateException("No existe el rol con id 2 (CLIENTE)."));

        // 1. Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getCorreo());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(rolCliente);
        usuario.setEstado(EstadoGeneral.ACTIVO);

        // 2. Crear Cliente asociado
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombreCompleto(dto.getNombreCompleto());
        cliente.setTipoDocumento(TipoDocumento.valueOf(dto.getTipoDocumento()));
        cliente.setDocumento(dto.getDocumento());
        cliente.setCorreo(dto.getCorreo());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setEstado(EstadoGeneral.ACTIVO);

        return clienteRepository.save(cliente);
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Cliente actualizarPerfil(Integer idCliente, RegistroClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombreCompleto(dto.getNombreCompleto());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());

        return clienteRepository.save(cliente);
    }

    public Cliente obtenerPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public java.util.Optional<Cliente> buscarPorCorreoOpt(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    public long contar() {
        return clienteRepository.count();
    }
}
