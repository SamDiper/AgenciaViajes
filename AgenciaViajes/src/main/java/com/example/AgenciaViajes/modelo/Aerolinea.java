package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aerolineas")
public class Aerolinea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aerolinea")
    private Long idAerolinea;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(name = "codigo_iata", length = 5)
    private String codigoIata;

    @Column(name = "precio_adicional", precision = 12, scale = 2)
    private BigDecimal precioAdicional = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "aerolinea_destinos",
        joinColumns = @JoinColumn(name = "id_aerolinea"),
        inverseJoinColumns = @JoinColumn(name = "id_destino")
    )
    private List<Destino> destinos = new ArrayList<>();

    public Aerolinea() {}

    public Long getIdAerolinea() { return idAerolinea; }
    public void setIdAerolinea(Long idAerolinea) { this.idAerolinea = idAerolinea; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoIata() { return codigoIata; }
    public void setCodigoIata(String codigoIata) { this.codigoIata = codigoIata; }

    public BigDecimal getPrecioAdicional() { return precioAdicional; }
    public void setPrecioAdicional(BigDecimal precioAdicional) { this.precioAdicional = precioAdicional; }

    public List<Destino> getDestinos() { return destinos; }
    public void setDestinos(List<Destino> destinos) { this.destinos = destinos; }
}
