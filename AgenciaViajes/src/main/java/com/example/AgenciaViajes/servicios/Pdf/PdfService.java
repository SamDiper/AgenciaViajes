package com.example.AgenciaViajes.servicios.Pdf;

import com.example.AgenciaViajes.modelo.DetalleReserva;
import com.example.AgenciaViajes.modelo.Reserva;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    private static final Color AZUL_MARCA = new Color(7, 59, 120); // #073B78
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", new Locale("es", "ES"));

    public byte[] generarItinerario(Reserva reserva) {
        try {
            Document documento = new Document(PageSize.A4, 40, 40, 60, 40);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            PdfWriter.getInstance(documento, salida);
            documento.open();

            // ---- Encabezado ----
            Font fuenteTitulo = new Font(Font.HELVETICA, 20, Font.BOLD, AZUL_MARCA);
            Paragraph titulo = new Paragraph("Traveling Colombia", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            Font fuenteSubtitulo = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.GRAY);
            Paragraph subtitulo = new Paragraph("Itinerario de viaje", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);

            // ---- Datos de la reserva ----
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

            // ---- Tabla de paquetes (uno por cada destino/detalle) ----
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

            // ---- Total ----
            Paragraph total = new Paragraph();
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(15);
            Font fuenteTotalEtiqueta = new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY);
            Font fuenteTotalValor = new Font(Font.HELVETICA, 16, Font.BOLD, AZUL_MARCA);
            total.add(new Chunk("Total a pagar: ", fuenteTotalEtiqueta));
            total.add(new Chunk(formatearMoneda(reserva.getTotal()), fuenteTotalValor));
            documento.add(total);

            // ---- Pie ----
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

    private void agregarCelda(PdfPTable tabla, String texto, Font fuente, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(8);
        celda.setHorizontalAlignment(alineacion);
        tabla.addCell(celda);
    }

    private String formatearMoneda(java.math.BigDecimal valor) {
        if (valor == null) return "$0";
        return "$" + String.format("%,.0f", valor).replace(",", ".");
    }
}