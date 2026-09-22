package com.example.AgenciaViajes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntidadConfigDTO {
    private String nombre;
    private String descripcion;
    private String icono;
    private String url;
    private long totalRegistros;
    private String sufijo;
}
 
