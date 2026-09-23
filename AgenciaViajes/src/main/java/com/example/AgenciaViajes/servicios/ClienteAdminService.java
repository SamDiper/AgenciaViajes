package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.modelo.Rol;
import com.example.AgenciaViajes.modelo.Usuario;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import com.example.AgenciaViajes.repositorio.RolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteAdminService {

    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteAdminService(ClienteRepository clienteRepository,
                                RolRepository rolRepository,
                                PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
    }

    public Cliente crear(Cliente datos, String contrasenaPlano) {
        Rol rolCliente = rolRepository.findByNombreRol("CLIENTE")
                .orElseThrow(() -> new RuntimeException("No existe el rol CLIENTE. Créalo primero en la tabla roles."));

        Usuario usuario = new Usuario();
        usuario.setCorreo(datos.getCorreo());
        usuario.setNombreUsuario(datos.getNombreCompleto());
        usuario.setContrasenaHash(passwordEncoder.encode(contrasenaPlano));
        usuario.setEstado(EstadoGeneral.ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setRol(rolCliente);

        datos.setUsuario(usuario);
        datos.setFechaRegistro(LocalDateTime.now());
        if (datos.getEstado() == null) {
            datos.setEstado(EstadoGeneral.ACTIVO);
        }

        return clienteRepository.save(datos); 
    }

    public Cliente actualizar(Integer id, Cliente datos) {
        Cliente existente = buscarPorId(id);

        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setTipoDocumento(datos.getTipoDocumento());
        existente.setDocumento(datos.getDocumento());
        existente.setCorreo(datos.getCorreo());
        existente.setTelefono(datos.getTelefono());
        existente.setDireccion(datos.getDireccion());
        existente.setEstado(datos.getEstado());

        existente.getUsuario().setCorreo(datos.getCorreo());

        return clienteRepository.save(existente);
    }

    public void eliminar(Integer id) {
        clienteRepository.deleteById(id); 
    }
}