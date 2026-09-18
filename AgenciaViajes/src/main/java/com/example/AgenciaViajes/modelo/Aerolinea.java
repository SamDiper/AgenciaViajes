package com.example.AgenciaViajes.modelo;

import jakarta.persistence.*;

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

    public Aerolinea() {}

    public Long getIdAerolinea() { return idAerolinea; }
    public void setIdAerolinea(Long idAerolinea) { this.idAerolinea = idAerolinea; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoIata() { return codigoIata; }
    public void setCodigoIata(String codigoIata) { this.codigoIata = codigoIata; }
}
