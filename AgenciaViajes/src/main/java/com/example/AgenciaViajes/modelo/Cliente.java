package com.example.AgenciaViajes.modelo;

import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;
import com.example.AgenciaViajes.modelo.Enum.TipoDocumento;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 150) 
    private String direccion;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeneral estado;
}
