package com.example.AgenciaViajes.servicios.Pdf;

import com.example.AgenciaViajes.modelo.DetalleReserva;
import com.example.AgenciaViajes.modelo.Factura;
import com.example.AgenciaViajes.modelo.Enum.EstadoReserva;
import com.example.AgenciaViajes.modelo.Reserva;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;

@Service
public class PdfService {

    private static final Color AZUL_MARCA = new Color(1, 38, 83); // #012653 Institucional
    private static final Color AZUL_ACENTO = new Color(11, 99, 229); // #0B63E5
    private static final Color GRIS_BORDE = new Color(226, 232, 240); // #E2E8F0
    private static final Color GRIS_TEXTO = new Color(100, 116, 139); // #64748B
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", new Locale("es", "ES"));

    private void agregarLogo(Document documento) {
        try {
            ClassPathResource imgResource = new ClassPathResource("static/assets/Traveling colombia.png");
            if (imgResource.exists()) {
                Image logo = Image.getInstance(imgResource.getInputStream().readAllBytes());
                logo.scaleToFit(170, 50);
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.setSpacingAfter(10);
                documento.add(logo);
                return;
            }
        } catch (Exception ignored) {
        }
        Font fuenteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, AZUL_MARCA);
        Paragraph titulo = new Paragraph("Traveling Colombia", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        documento.add(titulo);
    }

    public byte[] generarItinerario(Reserva reserva) {
        try {
            Document documento = new Document(PageSize.A4, 40, 40, 60, 40);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarLogo(documento);

            Font fuenteSubtitulo = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.GRAY);
            Paragraph subtitulo = new Paragraph("Itinerario de viaje", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);

            Font fuenteEtiqueta = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
            Font fuenteValor = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);

            Paragraph infoReserva = new Paragraph();
            infoReserva.add(new Chunk("Reserva N°: ", fuenteEtiqueta));
            infoReserva.add(new Chunk("#RES-" + reserva.getId() + "\n", fuenteValor));
            infoReserva.add(new Chunk("Cliente: ", fuenteEtiqueta));
            infoReserva.add(new Chunk(reserva.getCliente().getNombreCompleto() + "\n", fuenteValor));
            infoReserva.add(new Chunk("Fecha de reserva: ", fuenteEtiqueta));
            infoReserva.add(new Chunk(
                    reserva.getFechaReserva() != null ? reserva.getFechaReserva().format(FORMATO_FECHA) : "-", fuenteValor));
            infoReserva.setSpacingAfter(20);
            documento.add(infoReserva);

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{3f, 2.5f, 2f, 2f});

            Font fuenteHeader = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] encabezados = {"Paquete / Destino", "Fecha de viaje", "Personas", "Subtotal"};
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteHeader));
                celda.setBackgroundColor(AZUL_MARCA);
                celda.setPadding(8);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(celda);
            }

            Font fuenteCelda = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            for (DetalleReserva detalle : reserva.getDetalles()) {
                String nombreDestino = detalle.getPaquete().getNombre() + "\n" + detalle.getPaquete().getDestino().getNombre();
                agregarCelda(tabla, nombreDestino, fuenteCelda, Element.ALIGN_LEFT);
                agregarCelda(tabla, detalle.getFechaViaje() != null ? detalle.getFechaViaje().format(FORMATO_FECHA) : "-", fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, String.valueOf(detalle.getCantidadPersonas()), fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, formatearMoneda(detalle.getSubtotal()), fuenteCelda, Element.ALIGN_RIGHT);
            }
            documento.add(tabla);

            Paragraph total = new Paragraph();
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(15);
            Font fuenteTotalEtiqueta = new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY);
            Font fuenteTotalValor = new Font(Font.HELVETICA, 16, Font.BOLD, AZUL_MARCA);
            total.add(new Chunk("Total a pagar: ", fuenteTotalEtiqueta));
            total.add(new Chunk(formatearMoneda(reserva.getTotal()), fuenteTotalValor));
            documento.add(total);

            Paragraph pie = new Paragraph(
                    "\n\nGracias por reservar con Traveling Colombia. Este documento es tu comprobante de itinerario.",
                    new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
            pie.setSpacingBefore(40);
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

            documento.close();
            return salida.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generando el PDF del itinerario: " + e.getMessage(), e);
        }
    }

    public byte[] generarFactura(Factura factura) {
        try {
            Reserva reserva = factura.getReserva();

            Document documento = new Document(PageSize.A4, 40, 40, 60, 40);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarLogo(documento);

            Font fuenteSubtitulo = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.GRAY);
            Paragraph subtitulo = new Paragraph("Factura de venta " + factura.getNumero(), fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);

            Font fuenteEtiqueta = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
            Font fuenteValor = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);

            Paragraph info = new Paragraph();
            info.add(new Chunk("Factura N°: ", fuenteEtiqueta));
            info.add(new Chunk(factura.getNumero() + "\n", fuenteValor));
            info.add(new Chunk("Fecha de emisión: ", fuenteEtiqueta));
            info.add(new Chunk(factura.getFechaEmision() != null ? factura.getFechaEmision().format(FORMATO_FECHA) : "-", fuenteValor));
            info.add(new Chunk("\nReserva N°: ", fuenteEtiqueta));
            info.add(new Chunk("#RES-" + reserva.getId() + "\n", fuenteValor));
            info.add(new Chunk("Cliente: ", fuenteEtiqueta));
            info.add(new Chunk(reserva.getCliente().getNombreCompleto() + "\n", fuenteValor));
            info.add(new Chunk("Documento: ", fuenteEtiqueta));
            info.add(new Chunk(reserva.getCliente().getTipoDocumento() + " " + reserva.getCliente().getDocumento() + "\n", fuenteValor));
            info.add(new Chunk("Correo: ", fuenteEtiqueta));
            info.add(new Chunk(reserva.getCliente().getCorreo() + "\n", fuenteValor));
            info.add(new Chunk("Método de pago: ", fuenteEtiqueta));
            String metodo = factura.getMetodoPago() != null ? factura.getMetodoPago() : "-";
            if (factura.getUltimosDigitos() != null) {
                metodo += " terminada en " + factura.getUltimosDigitos();
            }
            info.add(new Chunk(metodo, fuenteValor));
            info.setSpacingAfter(20);
            documento.add(info);

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{4f, 1.5f, 2.2f, 2.2f});

            Font fuenteHeader = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] encabezados = {"Descripción", "Personas", "Valor unitario", "Subtotal"};
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, fuenteHeader));
                celda.setBackgroundColor(AZUL_MARCA);
                celda.setPadding(8);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(celda);
            }

            Font fuenteCelda = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            for (DetalleReserva detalle : reserva.getDetalles()) {
                if (detalle.getEstado() == EstadoReserva.CANCELADA) {
                    continue;
                }
                String descripcion = detalle.getPaquete().getNombre() + " - " + detalle.getPaquete().getDestino().getNombre();
                if (detalle.getFechaViaje() != null) {
                    descripcion += "\nViaje: " + detalle.getFechaViaje().format(FORMATO_FECHA);
                }
                agregarCelda(tabla, descripcion, fuenteCelda, Element.ALIGN_LEFT);
                agregarCelda(tabla, String.valueOf(detalle.getCantidadPersonas()), fuenteCelda, Element.ALIGN_CENTER);
                agregarCelda(tabla, formatearMoneda(detalle.getPrecioUnitario()), fuenteCelda, Element.ALIGN_RIGHT);
                agregarCelda(tabla, formatearMoneda(detalle.getSubtotal()), fuenteCelda, Element.ALIGN_RIGHT);
            }
            documento.add(tabla);

            Font fuenteTotalEtiqueta = new Font(Font.HELVETICA, 11, Font.BOLD, Color.DARK_GRAY);
            Font fuenteTotalValor = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
            Font fuenteGranTotal = new Font(Font.HELVETICA, 16, Font.BOLD, AZUL_MARCA);

            Paragraph totales = new Paragraph();
            totales.setAlignment(Element.ALIGN_RIGHT);
            totales.setSpacingBefore(15);
            totales.add(new Chunk("Subtotal: ", fuenteTotalEtiqueta));
            totales.add(new Chunk(formatearMoneda(factura.getSubtotal()) + "\n", fuenteTotalValor));
            totales.add(new Chunk("IVA (19%): ", fuenteTotalEtiqueta));
            totales.add(new Chunk(formatearMoneda(factura.getIva()) + "\n", fuenteTotalValor));
            totales.add(new Chunk("Total pagado: ", fuenteTotalEtiqueta));
            totales.add(new Chunk(formatearMoneda(factura.getTotal()), fuenteGranTotal));
            documento.add(totales);

            Paragraph pie = new Paragraph(
                    "\n\nGracias por viajar con Traveling Colombia.\n"
                            + "Documento generado en un entorno de simulación, sin validez fiscal.",
                    new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
            pie.setSpacingBefore(40);
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

            documento.close();
            return salida.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generando el PDF de la factura: " + e.getMessage(), e);
        }
    }

    public byte[] generarReporteDashboard(com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO stats,
                                          java.time.LocalDate desde,
                                          java.time.LocalDate hasta,
                                          String categoria) {
        try {
            Document documento = new Document(PageSize.A4, 36, 36, 38, 44);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(documento, salida);

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document doc) {
                    PdfContentByte cb = writer.getDirectContent();
                    cb.saveState();

                    cb.setColorFill(AZUL_MARCA);
                    cb.rectangle(36, doc.getPageSize().getHeight() - 18, doc.getPageSize().getWidth() - 72, 3);
                    cb.fill();

                    cb.setColorStroke(GRIS_BORDE);
                    cb.setLineWidth(0.8f);
                    cb.moveTo(36, 34);
                    cb.lineTo(doc.getPageSize().getWidth() - 36, 34);
                    cb.stroke();

                    Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, GRIS_TEXTO);
                    Phrase pLeft = new Phrase("Traveling Colombia • Informe Ejecutivo de Analítica y Gestión", fontFooter);
                    Phrase pRight = new Phrase("Pág. " + writer.getPageNumber(), fontFooter);

                    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, pLeft, 36, 22, 0);
                    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, pRight, doc.getPageSize().getWidth() - 36, 22, 0);

                    cb.restoreState();
                }
            });

            documento.open();

            agregarLogo(documento);

            Font fuenteSubtitulo = new Font(Font.HELVETICA, 13, Font.BOLD, AZUL_MARCA);
            Paragraph subtitulo = new Paragraph("Informe Ejecutivo de Ventas & Analítica", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(8);
            documento.add(subtitulo);

            Font fuenteFiltro = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
            String periodoTexto = "Periodo: " + (desde != null ? desde.format(FORMATO_FECHA) : "Inicio") + " al " + (hasta != null ? hasta.format(FORMATO_FECHA) : "Hoy");
            String catTexto = "Categoría: " + (categoria != null && !categoria.isBlank() ? categoria : "Todas");
            String genTexto = "Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            Paragraph infoFiltros = new Paragraph(periodoTexto + "  |  " + catTexto + "  |  " + genTexto, fuenteFiltro);
            infoFiltros.setAlignment(Element.ALIGN_CENTER);
            infoFiltros.setSpacingAfter(18);
            documento.add(infoFiltros);

            PdfPTable tablaKpis = new PdfPTable(4);
            tablaKpis.setWidthPercentage(100);
            tablaKpis.setSpacingAfter(20);

            Font fuenteKpiTitulo = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);
            Font fuenteKpiValor = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_MARCA);

            String[] kpiHeaders = {"INGRESOS TOTALES", "TOTAL RESERVAS", "CONFIRMADAS", "CLIENTES ACTIVOS"};
            for (String h : kpiHeaders) {
                PdfPCell c = new PdfPCell(new Phrase(h, fuenteKpiTitulo));
                c.setBackgroundColor(AZUL_MARCA);
                c.setPadding(6);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaKpis.addCell(c);
            }

            agregarCelda(tablaKpis, formatearMoneda(stats.getTotalIngresos()), fuenteKpiValor, Element.ALIGN_CENTER);
            agregarCelda(tablaKpis, String.valueOf(stats.getTotalReservas()), fuenteKpiValor, Element.ALIGN_CENTER);
            agregarCelda(tablaKpis, String.valueOf(stats.getTotalConfirmadas()), fuenteKpiValor, Element.ALIGN_CENTER);
            agregarCelda(tablaKpis, String.valueOf(stats.getTotalClientes()), fuenteKpiValor, Element.ALIGN_CENTER);
            documento.add(tablaKpis);

            Font fuenteSeccion = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_MARCA);
            Paragraph sec1 = new Paragraph("1. Ventas por Destino", fuenteSeccion);
            sec1.setSpacingBefore(10);
            sec1.setSpacingAfter(8);
            documento.add(sec1);

            PdfPTable tablaDestinos = new PdfPTable(5);
            tablaDestinos.setWidthPercentage(100);
            tablaDestinos.setWidths(new float[]{3.5f, 2f, 2f, 1.5f, 2.5f});
            tablaDestinos.setSpacingAfter(15);

            Font fuenteHeader = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            String[] hDest = {"Destino", "Categoría", "Ciudad", "Reservas", "Total Ventas"};
            for (String h : hDest) {
                PdfPCell c = new PdfPCell(new Phrase(h, fuenteHeader));
                c.setBackgroundColor(AZUL_MARCA);
                c.setPadding(6);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaDestinos.addCell(c);
            }

            Font fuenteFila = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
            for (com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO.VentaDestinoDTO d : stats.getVentasPorDestino()) {
                agregarCelda(tablaDestinos, d.getNombreDestino(), fuenteFila, Element.ALIGN_LEFT);
                agregarCelda(tablaDestinos, d.getCategoria(), fuenteFila, Element.ALIGN_CENTER);
                agregarCelda(tablaDestinos, d.getCiudad(), fuenteFila, Element.ALIGN_CENTER);
                agregarCelda(tablaDestinos, String.valueOf(d.getTotalReservas()), fuenteFila, Element.ALIGN_CENTER);
                agregarCelda(tablaDestinos, formatearMoneda(d.getTotalVentas()), fuenteFila, Element.ALIGN_RIGHT);
            }
            if (stats.getVentasPorDestino().isEmpty()) {
                PdfPCell c = new PdfPCell(new Phrase("No hay ventas de destinos en este período.", fuenteFila));
                c.setColspan(5);
                c.setPadding(8);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaDestinos.addCell(c);
            }
            documento.add(tablaDestinos);

            Paragraph sec2 = new Paragraph("2. Principales Clientes (Fidelidad)", fuenteSeccion);
            sec2.setSpacingBefore(10);
            sec2.setSpacingAfter(8);
            documento.add(sec2);

            PdfPTable tablaClientes = new PdfPTable(4);
            tablaClientes.setWidthPercentage(100);
            tablaClientes.setWidths(new float[]{3.5f, 2f, 2f, 2.5f});
            tablaClientes.setSpacingAfter(15);

            String[] hCli = {"Cliente", "Documento", "Confirmadas / Totales", "Total Invertido"};
            for (String h : hCli) {
                PdfPCell c = new PdfPCell(new Phrase(h, fuenteHeader));
                c.setBackgroundColor(AZUL_MARCA);
                c.setPadding(6);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaClientes.addCell(c);
            }

            for (com.example.AgenciaViajes.dto.dashboard.DashboardStatsDTO.VentaClienteDTO c : stats.getClientesFrecuentes()) {
                agregarCelda(tablaClientes, c.getNombreCliente(), fuenteFila, Element.ALIGN_LEFT);
                agregarCelda(tablaClientes, c.getDocumento() != null ? c.getDocumento() : "-", fuenteFila, Element.ALIGN_CENTER);
                agregarCelda(tablaClientes, c.getReservasConfirmadas() + " / " + c.getReservasTotales(), fuenteFila, Element.ALIGN_CENTER);
                agregarCelda(tablaClientes, formatearMoneda(c.getTotalGastado()), fuenteFila, Element.ALIGN_RIGHT);
            }
            if (stats.getClientesFrecuentes().isEmpty()) {
                PdfPCell c = new PdfPCell(new Phrase("No hay registros de clientes en este período.", fuenteFila));
                c.setColspan(4);
                c.setPadding(8);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaClientes.addCell(c);
            }
            documento.add(tablaClientes);

            documento.close();
            return salida.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generando el PDF del informe: " + e.getMessage(), e);
        }
    }

    private void agregarCelda(PdfPTable tabla, String texto, Font fuente, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(6);
        celda.setHorizontalAlignment(alineacion);
        tabla.addCell(celda);
    }

    private String formatearMoneda(java.math.BigDecimal valor) {
        if (valor == null) return "$0";
        return "$" + String.format("%,.0f", valor).replace(",", ".");
    }
}