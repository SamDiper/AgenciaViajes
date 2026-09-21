package com.example.AgenciaViajes.modelo.carrito;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemCarrito implements Serializable {
 
    private static final long serialVersionUID = 1L;
 
    private Long idPaquete;
    private String nombre;
    private BigDecimal precioUnitario;
    private int personas;
 
    public ItemCarrito() {
    }
 
    public ItemCarrito(Long idPaquete, String nombre, BigDecimal precioUnitario, int personas) {
        this.idPaquete = idPaquete;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.personas = personas;
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
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
}
