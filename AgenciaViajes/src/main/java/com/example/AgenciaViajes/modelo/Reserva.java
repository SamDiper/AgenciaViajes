package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    private LocalDateTime fechaReserva = LocalDateTime.now();

    private LocalDate fechaViaje;

    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_gestiona")
    private Usuario usuarioGestiona;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReserva> detalles = new ArrayList<>();

    public Reserva() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDate getFechaViaje() {
        return fechaViaje;
    }

    public void setFechaViaje(LocalDate fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public Usuario getUsuarioGestiona() {
        return usuarioGestiona;
    }

    public void setUsuarioGestiona(Usuario usuarioGestiona) {
        this.usuarioGestiona = usuarioGestiona;
    }

    public List<DetalleReserva> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReserva> detalles) {
        this.detalles = detalles;
    }


    public void addDetalle(DetalleReserva d) {
        d.setReserva(this);
        detalles.add(d);
    }

    public void recalcularTotal() {
        total = detalles.stream()
                .map(DetalleReserva::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}