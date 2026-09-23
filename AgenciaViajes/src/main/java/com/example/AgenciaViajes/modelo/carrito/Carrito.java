package com.example.AgenciaViajes.modelo.carrito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.example.AgenciaViajes.modelo.Aerolinea;
import com.example.AgenciaViajes.modelo.Hotel;
import com.example.AgenciaViajes.modelo.Paquete;

@Component
@SessionScope
public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, ItemCarrito> items = new LinkedHashMap<>();

    public void agregar(Paquete paquete, Aerolinea aerolinea, Hotel hotel, int personas) {
        ItemCarrito nuevo = new ItemCarrito(paquete, aerolinea, hotel, personas);
        ItemCarrito existente = items.get(nuevo.getClave());
        if (existente != null) {
            existente.setPersonas(existente.getPersonas() + personas);
        } else {
            items.put(nuevo.getClave(), nuevo);
        }
    }

    public void actualizarPersonas(String clave, int personas) {
        ItemCarrito item = items.get(clave);
        if (item != null && personas > 0) {
            item.setPersonas(personas);
        }
    }

    public void quitar(String clave) {
        items.remove(clave);
    }

    public void vaciar() {
        items.clear();
    }

    public Collection<ItemCarrito> getItems() {
        return items.values();
    }

    public boolean isVacio() {
        return items.isEmpty();
    }

    public int getCantidadItems() {
        return items.size();
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}