package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Rol;
import com.example.AgenciaViajes.modelo.Usuario;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.repositorio.RolRepository;
import com.example.AgenciaViajes.repositorio.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               RolRepository rolRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    public Usuario crear(String nombreUsuario, String correo, String contrasena) {
        Rol rolAdmin = rolRepository.findByNombreRol("ADMIN")
                .orElseThrow(() -> new RuntimeException("No existe el rol ADMIN. Créalo primero en la tabla roles."));

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(contrasena)); // se guarda encriptada
        usuario.setRol(rolAdmin);
        usuario.setEstado(EstadoGeneral.ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Integer id, String nombreUsuario, String correo,
                              String contrasena, EstadoGeneral estado) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);

        if (contrasena != null && !contrasena.isBlank()) {
            usuario.setContrasenaHash(passwordEncoder.encode(contrasena));
        }
        if (estado != null) {
            usuario.setEstado(estado);
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }
}