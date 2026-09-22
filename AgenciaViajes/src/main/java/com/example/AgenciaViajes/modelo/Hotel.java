package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.example.AgenciaViajes.modelo.Enum.EstadoGeneral;

@Entity
@Table(name = "hoteles")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hotel")
    private Long idHotel;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Lob
    private String descripcion;

    @Column(nullable = false)
    private int estrellas = 3;

    @Column(name = "precio_adicional", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAdicional = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_destino", nullable = false)
    private Destino destino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeneral estado = EstadoGeneral.ACTIVO;

    public Hotel() {}

    public Long getIdHotel() { return idHotel; }
    public void setIdHotel(Long idHotel) { this.idHotel = idHotel; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }

    public BigDecimal getPrecioAdicional() { return precioAdicional; }
    public void setPrecioAdicional(BigDecimal precioAdicional) { this.precioAdicional = precioAdicional; }

    public Destino getDestino() { return destino; }
    public void setDestino(Destino destino) { this.destino = destino; }

    public EstadoGeneral getEstado() { return estado; }
    public void setEstado(EstadoGeneral estado) { this.estado = estado; }
}
