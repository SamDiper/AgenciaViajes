package com.example.AgenciaViajes.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // KPIs Generales
    @Builder.Default
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @Builder.Default
    private long totalReservas = 0;

    @Builder.Default
    private long totalConfirmadas = 0;

    @Builder.Default
    private long totalPendientes = 0;

    @Builder.Default
    private long totalCanceladas = 0;

    @Builder.Default
    private long totalClientes = 0;

    // Listas para Informes y Gráficos
    @Builder.Default
    private List<VentaDestinoDTO> ventasPorDestino = new ArrayList<>();

    @Builder.Default
    private List<VentaClienteDTO> clientesFrecuentes = new ArrayList<>();

    @Builder.Default
    private List<VentaPaqueteDTO> ventasPorPaquete = new ArrayList<>();

    @Builder.Default
    private List<VentaTemporadaDTO> ingresosPorTemporada = new ArrayList<>();

    @Builder.Default
    private List<CategoriaVentaDTO> distribucionCategorias = new ArrayList<>();

    @Builder.Default
    private List<TransaccionResumenDTO> ultimasTransacciones = new ArrayList<>();

    // Sub-DTO: Ventas por Destino
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaDestinoDTO {
        private Long idDestino;
        private String nombreDestino;
        private String categoria;
        private String ciudad;
        private long totalReservas;
        private BigDecimal totalVentas;
        private double porcentaje;
        private String imagenUrl;
    }

    // Sub-DTO: Ventas por Cliente / Clientes Frecuentes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaClienteDTO {
        private Integer idCliente;
        private String nombreCliente;
        private String documento;
        private String correo;
        private String telefono;
        private long reservasConfirmadas;
        private long reservasTotales;
        private BigDecimal totalGastado;
        private LocalDateTime ultimaFechaReserva;
    }

    // Sub-DTO: Ventas por Paquete Turístico
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaPaqueteDTO {
        private Long idPaquete;
        private String nombrePaquete;
        private String nombreDestino;
        private String categoriaDestino;
        private BigDecimal precioBase;
        private int duracionDias;
        private long cantidadVendida;
        private BigDecimal totalIngresos;
    }

    // Sub-DTO: Ingresos por Temporada / Periodo
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaTemporadaDTO {
        private String etiqueta; // Ej: "Enero 2026", "Temporada Alta (Jun-Jul)", etc.
        private String periodoClave; // Ej: "2026-01"
        private BigDecimal ingresos;
        private long cantidadReservas;
    }

    // Sub-DTO: Distribución por Categoría de Destino
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaVentaDTO {
        private Long idCategoria;
        private String nombreCategoria;
        private long totalReservas;
        private BigDecimal totalIngresos;
        private double porcentaje;
    }

    // Sub-DTO: Transacción Resumen
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransaccionResumenDTO {
        private Integer idReserva;
        private String cliente;
        private String destino;
        private String paquete;
        private LocalDateTime fechaReserva;
        private LocalDate fechaViaje;
        private BigDecimal total;
        private String estado;
    }
}
