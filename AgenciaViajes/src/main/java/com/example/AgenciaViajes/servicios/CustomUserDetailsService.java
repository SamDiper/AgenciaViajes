package com.example.AgenciaViajes.servicios;

import com.example.AgenciaViajes.modelo.Cliente;
import com.example.AgenciaViajes.repositorio.ClienteRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    public CustomUserDetailsService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Busca al cliente por su correo
        Cliente cliente = clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró usuario asociado al correo: " + correo));

        // Retorna las credenciales extrayéndolas de la entidad Usuario asociada
        return new User(
                cliente.getUsuario().getCorreo(),
                cliente.getUsuario().getContrasenaHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + cliente.getUsuario().getRol().getNombreRol()))
        );
    }
}
