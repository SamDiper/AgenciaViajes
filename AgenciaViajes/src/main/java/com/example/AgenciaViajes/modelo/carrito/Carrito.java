package com.example.AgenciaViajes.modelo.carrito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.example.AgenciaViajes.modelo.Paquete;

@Component
@SessionScope
public class Carrito implements Serializable{

    private static final long serialVersionUID = 1L;
 
    private final Map<Long, ItemCarrito> items = new LinkedHashMap<>(); 
 
    public void agregar(Paquete paquete, int personas) {
        ItemCarrito existente = items.get(paquete.getIdPaquete());
        if (existente != null) {
            existente.setPersonas(existente.getPersonas() + personas);
        } else {
            items.put(paquete.getIdPaquete(),
                    new ItemCarrito(paquete.getIdPaquete(), paquete.getNombre(), paquete.getPrecioBase(), personas));
        }
    }
 
    public void actualizarPersonas(Long idPaquete, int personas) {
        ItemCarrito item = items.get(idPaquete);
        if (item != null && personas > 0) {
            item.setPersonas(personas);
        }
    }
 
    public void quitar(Integer idPaquete) {
        items.remove(idPaquete);
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
