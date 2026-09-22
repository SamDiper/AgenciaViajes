package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer id;

    // Ej: FAC-000001
    @Column(unique = true, length = 20)
    private String numero;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    // Una reserva tiene una sola factura
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reserva", nullable = false, unique = true)
    private Reserva reserva;

    // Valor sin IVA
    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal iva = BigDecimal.ZERO;

    // Lo que pagó el cliente (IVA incluido)
    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    // Solo los últimos 4 dígitos de la tarjeta, nunca el número completo
    @Column(name = "ultimos_digitos", length = 4)
    private String ultimosDigitos;

    public Factura() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getUltimosDigitos() { return ultimosDigitos; }
    public void setUltimosDigitos(String ultimosDigitos) { this.ultimosDigitos = ultimosDigitos; }
}
