  package com.example.AgenciaViajes.controllers.api;

import com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO;
import com.example.AgenciaViajes.servicios.DashboardService;
import com.example.AgenciaViajes.servicios.Pdf.PdfService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardRestController {

    private final DashboardService dashboardService;
    private final PdfService pdfService;

    public AdminDashboardRestController(DashboardService dashboardService, PdfService pdfService) {
        this.dashboardService = dashboardService;
        this.pdfService = pdfService;
    }

    @GetMapping("/datos")
    public ResponseEntity<DashboardStatsDTO> obtenerDatos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idCategoria) {

        DashboardStatsDTO stats = dashboardService.obtenerEstadisticas(fechaInicio, fechaFin, idCategoria);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idCategoria) {

        DashboardStatsDTO stats = dashboardService.obtenerEstadisticas(fechaInicio, fechaFin, idCategoria);
        String nombreCategoria = dashboardService.obtenerNombreCategoria(idCategoria);
        byte[] pdfBytes = pdfService.generarReporteDashboard(stats, fechaInicio, fechaFin, nombreCategoria);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas_dashboard.pdf")
                .body(pdfBytes);
    }
}
