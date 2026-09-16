package com.example.AgenciaViajes.modelo;

import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeneral estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}



