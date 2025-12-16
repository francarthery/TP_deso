package tp_hotel.tp_hotel.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.NotaCreditoNoExistenteException;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.NotaCredito;
import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.model.ResponsablePago;
import tp_hotel.tp_hotel.model.TipoFactura;
import tp_hotel.tp_hotel.repository.FacturaRepository;
import tp_hotel.tp_hotel.repository.NotaCreditoRepository;

@Service
public class GestorNotaCredito {
    private final NotaCreditoRepository notaCreditoRepository;
    private final FacturaRepository facturaRepository;

    @Autowired
    public GestorNotaCredito(NotaCreditoRepository notaCreditoRepository, FacturaRepository facturaRepository){
        this.notaCreditoRepository = notaCreditoRepository;
        this.facturaRepository = facturaRepository;
    }

    public String obtenerUltimoNumero(){
        String ultimoNumeroStr = notaCreditoRepository.findUltimoNumero();
        long ultimoNumero = 0;
        if (ultimoNumeroStr != null) {
            ultimoNumero = Long.parseLong(ultimoNumeroStr);
        }
        String nuevoNumero = String.format("%010d", ultimoNumero + 1);
        
        return nuevoNumero;
    }

    @Transactional
	public NotaCredito crearNotaCredito(List<Integer> facturasIds) {
        List<Factura> facturas = facturaRepository.findAllById(facturasIds);
        
        if(facturas.size() != facturasIds.size()){
            throw new FacturasNoExistentesException("Hay factura/s no existente/s.");
        }
        
        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setNumero(obtenerUltimoNumero());
        notaCredito.setFecha(LocalDate.now());
        notaCredito.setFacturas(facturas);
        notaCredito.calcularMonto();

        notaCreditoRepository.save(notaCredito);
    
        for(Factura f : facturas){
            f.setEstado(EstadoFactura.ANULADA);
        }

        return notaCredito;
    }

    public byte[] generarPDFNotaCredito(Integer notaCreditoId){
        NotaCredito notaCredito = notaCreditoRepository.findById(notaCreditoId)
        .orElseThrow(() -> new NotaCreditoNoExistenteException("Nota de crédito no encontrada"));

        if(notaCredito.getFacturas().isEmpty()){
             return new byte[0];
        }
        Factura primeraFactura = notaCredito.getFacturas().get(0);
        TipoFactura tipoFactura = primeraFactura.getTipo();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fuentes
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font fontType = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);

            // --- CABECERA PRINCIPAL ---
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{4, 1, 4});

            // 1. Columna Izquierda: Emisor
            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(Rectangle.BOX);
            cellLeft.setPadding(5);
            
            Paragraph pLogo = new Paragraph("PREMIER", fontTitle);
            pLogo.setAlignment(Element.ALIGN_CENTER);
            cellLeft.addElement(pLogo);
            cellLeft.addElement(new Paragraph("Razón Social: PREMIER SRL", fontBold));
            cellLeft.addElement(new Paragraph("Domicilio Comercial: Calle Falsa 123, CABA", fontNormal));
            cellLeft.addElement(new Paragraph("Condición frente al IVA: Responsable Inscripto", fontBold));
            headerTable.addCell(cellLeft);

            // 2. Columna Central: Tipo de Nota de Credito
            PdfPCell cellCenter = new PdfPCell();
            cellCenter.setBorder(Rectangle.BOX);
            cellCenter.setVerticalAlignment(Element.ALIGN_TOP);
            
            Paragraph pOriginal = new Paragraph("ORIGINAL", fontSmall);
            pOriginal.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(pOriginal);
            
            Paragraph pLetra = new Paragraph(tipoFactura.toString(), fontType);
            pLetra.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(pLetra);
            
            Paragraph pCod = new Paragraph("COD. " + (tipoFactura == TipoFactura.A ? "03" : "08"), fontSmall);
            pCod.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(pCod);
            
            headerTable.addCell(cellCenter);

            // 3. Columna Derecha: Datos Nota Credito
            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(Rectangle.BOX);
            cellRight.setPadding(5);
            
            Paragraph pNcTitle = new Paragraph("NOTA DE CRÉDITO", fontTitle);
            pNcTitle.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(pNcTitle);
            
            cellRight.addElement(new Paragraph("Punto de Venta: 00007  Comp. Nro: " + notaCredito.getNumero(), fontBold));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            cellRight.addElement(new Paragraph("Fecha de Emisión: " + sdf.format(java.sql.Date.valueOf(notaCredito.getFecha())), fontBold));
            cellRight.addElement(new Paragraph("CUIT: 30-71093952-3", fontNormal));
            cellRight.addElement(new Paragraph("Ingresos Brutos: 30-71093952-3", fontNormal));
            cellRight.addElement(new Paragraph("Fecha de Inicio de Actividades: 01/11/2013", fontNormal));
            headerTable.addCell(cellRight);

            document.add(headerTable);

            // --- PERIODO FACTURADO ---
            PdfPTable periodTable = new PdfPTable(1);
            periodTable.setWidthPercentage(100);
            PdfPCell periodCell = new PdfPCell();
            periodCell.setBorder(Rectangle.BOX);
            periodCell.setPadding(3);
            
            String fechaDesde = sdf.format(java.sql.Date.valueOf(primeraFactura.getEstadia().getCheckIn()));
            String fechaHasta = sdf.format(java.sql.Date.valueOf(primeraFactura.getEstadia().getCheckOut()));
            
            periodCell.addElement(new Paragraph("Período Facturado Desde: " + fechaDesde + "   Hasta: " + fechaHasta + "       Fecha de Vto. para el pago: " + sdf.format(java.sql.Date.valueOf(notaCredito.getFecha())), fontNormal));
            periodTable.addCell(periodCell);
            document.add(periodTable);

            // --- DATOS DEL CLIENTE ---
            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);
            clientTable.setWidths(new float[]{1, 3});

            ResponsablePago resp = primeraFactura.getResponsablePago();
            String nombreCliente = "";
            String cuitCliente = "";
            String direccionCliente = "";
            String condicionIva = "Consumidor Final";

            if (resp instanceof PersonaFisica) {
                PersonaFisica pf = (PersonaFisica) resp;
                nombreCliente = pf.getHuesped().getApellido() + " " + pf.getHuesped().getNombres();
                cuitCliente = pf.getHuesped().getNumeroDocumento();
                direccionCliente = pf.getHuesped().getDireccion().getCalle() + " " + pf.getHuesped().getDireccion().getNumero();
                if (pf.esResponsableInscripto()) condicionIva = "Responsable Inscripto";
            } else if (resp instanceof PersonaJuridica) {
                PersonaJuridica pj = (PersonaJuridica) resp;
                nombreCliente = pj.getRazonSocial();
                cuitCliente = pj.getCUIT();
                direccionCliente = pj.getDireccion().getCalle() + " " + pj.getDireccion().getNumero();
                condicionIva = "Responsable Inscripto";
            }

            addClientCell(clientTable, "CUIT: " + cuitCliente, fontBold);
            addClientCell(clientTable, "Apellido y Nombre / Razón Social: " + nombreCliente, fontBold);
            addClientCell(clientTable, "Condición frente al IVA: " + condicionIva, fontBold);
            addClientCell(clientTable, "Domicilio: " + direccionCliente, fontNormal);

            PdfPCell cellCondVenta = new PdfPCell(new Phrase("Condición de venta: Otra", fontNormal));
            cellCondVenta.setColspan(2);
            cellCondVenta.setBorder(Rectangle.BOX);
            cellCondVenta.setPadding(3);
            clientTable.addCell(cellCondVenta);

            document.add(clientTable);
            document.add(new Paragraph("\n"));

            // --- DETALLE ---
            float[] columnWidths = {1.5f, 4f, 1f, 1.5f, 2f, 1.5f, 2f, 1.5f, 2f};
            PdfPTable table = new PdfPTable(columnWidths.length);
            table.setWidthPercentage(100);
            table.setWidths(columnWidths);

            String[] headers = {"Código", "Producto / Servicio", "Cantidad", "U. medida", "Precio Unit.", "% Bonif", "Subtotal", "Alicuota IVA", "Subtotal c/IVA"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontSmall));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            double totalNetoGravado = 0;
            double totalIVA21 = 0;

            for (Factura f : notaCredito.getFacturas()) {
                double totalFactura = f.getTotal();
                
                // Desglose (Asumiendo 21% para simplificar visualización)
                double subtotalNeto = totalFactura / 1.21;
                double iva = totalFactura - subtotalNeto;
                
                table.addCell(new Phrase(f.getNumero(), fontSmall));
                table.addCell(new Phrase("Anulación Factura " + f.getNumero(), fontSmall));
                
                PdfPCell cellCant = new PdfPCell(new Phrase("1", fontSmall));
                cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellCant);
                
                table.addCell(new Phrase("unidades", fontSmall));
                
                PdfPCell cellPrecio = new PdfPCell(new Phrase(String.format("%.2f", subtotalNeto), fontSmall));
                cellPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellPrecio);
                
                table.addCell(new Phrase("0.00", fontSmall));
                
                PdfPCell cellSubNeto = new PdfPCell(new Phrase(String.format("%.2f", subtotalNeto), fontSmall));
                cellSubNeto.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellSubNeto);
                
                table.addCell(new Phrase("21%", fontSmall));
                
                PdfPCell cellSubTotal = new PdfPCell(new Phrase(String.format("%.2f", totalFactura), fontSmall));
                cellSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellSubTotal);

                totalNetoGravado += subtotalNeto;
                totalIVA21 += iva;
            }
            
            document.add(table);

            // --- TOTALES ---
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{6, 4});
            footerTable.setSpacingBefore(10);

            PdfPCell cellFooterLeft = new PdfPCell();
            cellFooterLeft.setBorder(Rectangle.BOX);
            
            // Table for "Otros Tributos" inside left footer
            PdfPTable tributosTable = new PdfPTable(3);
            tributosTable.setWidthPercentage(100);
            tributosTable.setWidths(new float[]{4, 1, 1});
            
            // Headers
            tributosTable.addCell(new Phrase("Descripción", fontSmall));
            tributosTable.addCell(new Phrase("Detalle", fontSmall));
            tributosTable.addCell(new Phrase("Importe", fontSmall));
            
            // Rows
            tributosTable.addCell(new Phrase("Per./Ret. de Impuesto a las Ganancias", fontSmall));
            tributosTable.addCell(new Phrase("", fontSmall));
            tributosTable.addCell(new Phrase("0,00", fontSmall));
            
            tributosTable.addCell(new Phrase("Per./Ret. de IVA", fontSmall));
            tributosTable.addCell(new Phrase("", fontSmall));
            tributosTable.addCell(new Phrase("0,00", fontSmall));
            
            tributosTable.addCell(new Phrase("Impuestos Internos", fontSmall));
            tributosTable.addCell(new Phrase("", fontSmall));
            tributosTable.addCell(new Phrase("0,00", fontSmall));
            
            tributosTable.addCell(new Phrase("Impuestos Municipales", fontSmall));
            tributosTable.addCell(new Phrase("", fontSmall));
            tributosTable.addCell(new Phrase("0,00", fontSmall));
            
            tributosTable.addCell(new Phrase("Importe Otros Tributos: $", fontBold));
            tributosTable.addCell(new Phrase("", fontSmall));
            tributosTable.addCell(new Phrase("0,00", fontBold));

            cellFooterLeft.addElement(new Paragraph("Otros Tributos", fontBold));
            cellFooterLeft.addElement(tributosTable);
            footerTable.addCell(cellFooterLeft);

            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setWidths(new float[]{6, 4});

            addTotalRow(totalsTable, "Importe Neto Gravado: $", totalNetoGravado, fontBold);
            addTotalRow(totalsTable, "IVA 27%: $", 0.0, fontNormal);
            addTotalRow(totalsTable, "IVA 21%: $", totalIVA21, fontNormal);
            addTotalRow(totalsTable, "IVA 10.5%: $", 0.0, fontNormal);
            addTotalRow(totalsTable, "IVA 5%: $", 0.0, fontNormal);
            addTotalRow(totalsTable, "IVA 2.5%: $", 0.0, fontNormal);
            addTotalRow(totalsTable, "IVA 0%: $", 0.0, fontNormal);
            addTotalRow(totalsTable, "Importe Otros Tributos: $", 0.0, fontNormal);
            
            PdfPCell cellTotalLabel = new PdfPCell(new Phrase("Importe Total: $", fontBold));
            cellTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalLabel.setBorder(Rectangle.TOP);
            totalsTable.addCell(cellTotalLabel);
            
            PdfPCell cellTotalValue = new PdfPCell(new Phrase(String.format("%.2f", totalNetoGravado + totalIVA21), fontBold));
            cellTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalValue.setBorder(Rectangle.TOP);
            totalsTable.addCell(cellTotalValue);

            PdfPCell cellFooterRight = new PdfPCell(totalsTable);
            cellFooterRight.setBorder(Rectangle.BOX);
            footerTable.addCell(cellFooterRight);

            document.add(footerTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addClientCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, double value, Font font) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(String.format("%.2f", value), font));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setBorder(Rectangle.NO_BORDER);
        table.addCell(cellValue);
    }
}
