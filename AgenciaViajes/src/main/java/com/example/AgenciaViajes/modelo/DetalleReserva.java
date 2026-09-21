package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_reserva")
public class DetalleReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paquete")
    private Paquete paquete;

    @Min(1)
    private Integer cantidadPersonas;

    private BigDecimal precioUnitario;   // copia del precio al momento de reservar

    private BigDecimal subtotal;         // precioUnitario * cantidadPersonas

    public DetalleReserva() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }

    public Integer getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(Integer cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}