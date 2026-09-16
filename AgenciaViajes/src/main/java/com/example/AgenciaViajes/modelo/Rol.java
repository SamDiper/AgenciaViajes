package com.example.AgenciaViajes.modelo;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import org.hibernate.annotations.Audited;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Audited.Table(name = "roles")
public class Rol {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;


    @Column(name = "nombre_rol", nullable = false, length = 50)
    private String nombreRol;
}
