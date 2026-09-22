package com.example.AgenciaViajes.modelo.carrito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;

/**
 * Línea del carrito: un paquete con la aerolínea y el hotel elegidos.
 * Guarda solo datos planos (ids, nombres y precio), nunca entidades JPA,
 * para evitar problemas de lazy loading al vivir en la sesión.
 */
public class ItemCarrito implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idPaquete;
    private String nombrePaquete;
    private String nombreDestino;

    private Long idAerolinea;          // null = sin aerolínea
    private String nombreAerolinea;

    private Long idHotel;              // null = sin hotel
    private String nombreHotel;

    private BigDecimal precioUnitario; // por persona, con extras de aerolínea y hotel
    private int personas;
    private LocalDate fechaViaje;

    public ItemCarrito() {
    }

    public ItemCarrito(Paquete paquete, Aerolinea aerolinea, Hotel hotel, int personas) {
        this.idPaquete = paquete.getIdPaquete();
        this.nombrePaquete = paquete.getNombre();
        this.nombreDestino = paquete.getDestino().getNombre();

        if (aerolinea != null) {
            this.idAerolinea = aerolinea.getIdAerolinea();
            this.nombreAerolinea = aerolinea.getNombre();
        }
        if (hotel != null) {
            this.idHotel = hotel.getIdHotel();
            this.nombreHotel = hotel.getNombre();
        }

        this.precioUnitario = calcularPrecioUnitario(paquete, aerolinea, hotel);
        this.personas = personas;
        this.fechaViaje = null;
    }

    /**
     * Fórmula única del precio por persona. La usan el carrito (para mostrar)
     * y ReservaService (para guardar), así nunca se desincronizan.
     * Debe coincidir con cómo calculas precioGranTotal en PaqueteController.
     */
    public static BigDecimal calcularPrecioUnitario(Paquete paquete, Aerolinea aerolinea, Hotel hotel) {
        BigDecimal precio = paquete.getPrecioBase();
        if (aerolinea != null && aerolinea.getPrecioAdicional() != null) {
            precio = precio.add(aerolinea.getPrecioAdicional());
        }
        if (hotel != null && hotel.getPrecioAdicional() != null) {
            precio = precio.add(hotel.getPrecioAdicional());
        }
        return precio;
    }

    /** Identifica la línea: mismo paquete con distinta aerolínea u hotel = línea distinta. */
    public String getClave() {
        return idPaquete + "-" + (idAerolinea != null ? idAerolinea : 0) + "-" + (idHotel != null ? idHotel : 0);
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(personas));
    }

    public Long getIdPaquete() {
        return idPaquete;
    }

    public void setIdPaquete(Long idPaquete) {
        this.idPaquete = idPaquete;
    }

    public String getNombrePaquete() {
        return nombrePaquete;
    }

    public void setNombrePaquete(String nombrePaquete) {
        this.nombrePaquete = nombrePaquete;
    }

    public String getNombreDestino() {
        return nombreDestino;
    }

    public void setNombreDestino(String nombreDestino) {
        this.nombreDestino = nombreDestino;
    }

    public Long getIdAerolinea() {
        return idAerolinea;
    }

    public void setIdAerolinea(Long idAerolinea) {
        this.idAerolinea = idAerolinea;
    }

    public String getNombreAerolinea() {
        return nombreAerolinea;
    }

    public void setNombreAerolinea(String nombreAerolinea) {
        this.nombreAerolinea = nombreAerolinea;
    }

    public Long getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(Long idHotel) {
        this.idHotel = idHotel;
    }

    public String getNombreHotel() {
        return nombreHotel;
    }

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getPersonas() {
        return personas;
    }

    public void setPersonas(int personas) {
        this.personas = personas;
    }

    public LocalDate getFechaViaje() { return fechaViaje; }
    public void setFechaViaje(LocalDate fechaViaje) { this.fechaViaje = fechaViaje; }
}