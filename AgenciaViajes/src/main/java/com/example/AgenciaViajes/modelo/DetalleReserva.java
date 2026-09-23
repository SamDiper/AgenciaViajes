package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aerolinea")
    private Aerolinea aerolinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hotel")
    private Hotel hotel;

    @Min(1)
    private Integer cantidadPersonas;

    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    @Column(nullable = false)
    private LocalDate fechaViaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    public DetalleReserva() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Paquete getPaquete() { return paquete; }
    public void setPaquete(Paquete paquete) { this.paquete = paquete; }
    public Aerolinea getAerolinea() { return aerolinea; }
    public void setAerolinea(Aerolinea aerolinea) { this.aerolinea = aerolinea; }
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
    public Integer getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public LocalDate getFechaViaje() { return fechaViaje; }
    public void setFechaViaje(LocalDate fechaViaje) { this.fechaViaje = fechaViaje; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
}