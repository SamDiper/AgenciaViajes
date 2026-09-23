package com.example.AgenciaViajes.servicios;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    
    private final String nombreCompleto; 

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String nombreCompleto) {
        super(username, password, authorities); 
        this.nombreCompleto = nombreCompleto;   
    }

    // El getter es lo que Thymeleaf usará para mostrar el nombre
    public String getNombreCompleto() {
        return nombreCompleto;
    }
}
