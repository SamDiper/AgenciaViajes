package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias_destino")
public class CategoriaDestino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    public CategoriaDestino() {}

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}