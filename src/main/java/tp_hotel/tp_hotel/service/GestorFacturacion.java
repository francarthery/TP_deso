package tp_hotel.tp_hotel.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import tp_hotel.tp_hotel.model.Consumo;
import tp_hotel.tp_hotel.model.DatosFacturaDTO;
import tp_hotel.tp_hotel.model.DetalleFactura;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.model.ResponsablePago;
import tp_hotel.tp_hotel.model.TipoFactura;
import tp_hotel.tp_hotel.repository.ConsumoRepository;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.FacturaRepository;
import tp_hotel.tp_hotel.repository.ResponsablePagoRepository;

@Service
public class GestorFacturacion {

    private final FacturaRepository facturaRepository;
    private final ConsumoRepository consumoRepository;
    private final ResponsablePagoRepository responsablePagoRepository;
    private final EstadiaRepository estadiaRepository;

    @Autowired
    public GestorFacturacion(FacturaRepository facturaRepository, ConsumoRepository consumoRepository, ResponsablePagoRepository responsablePagoRepository, EstadiaRepository estadiaRepository) {
        this.facturaRepository = facturaRepository;
        this.consumoRepository = consumoRepository;
        this.responsablePagoRepository = responsablePagoRepository;
        this.estadiaRepository = estadiaRepository;
    }

    public Factura obtenerFacturaPorId(Integer idFactura) {
        return facturaRepository.findById(idFactura)
            .orElseThrow(() -> new FacturasNoExistentesException("No se encontró la factura con ID: " + idFactura));
    }

    public List<Factura> obtenerFacturasPorHabitacionNoPagas(String numeroHabitacion) {
        List<Factura> facturas = facturaRepository.findByNumeroHabitacionYEstado(numeroHabitacion, EstadoFactura.PENDIENTE);
        if(facturas.isEmpty()) {
            throw new FacturasNoExistentesException("No se encontraron facturas pendientes de pago para la habitación: " + numeroHabitacion);
        } else return facturas;
    }

    public byte[] generarFactura(DatosFacturaDTO d) {
        Factura factura = new Factura();

        List<Consumo> consumos = consumoRepository.findAllById(d.getIdConsumos());

        Estadia estadia = estadiaRepository.findById(d.getIdEstadia())
            .orElseThrow(() -> new IllegalArgumentException("Estadía no encontrada: " + d.getIdEstadia()));
        
        ResponsablePago responsable = responsablePagoRepository.findById(d.getIdResponsablePago())
            .orElseThrow(() -> new IllegalArgumentException("Responsable de pago no encontrado: " + d.getIdResponsablePago()));
        
        if(responsable instanceof PersonaJuridica || 
            responsable instanceof PersonaFisica && ((PersonaFisica) responsable).esResponsableInscripto()) {
            factura.setTipo(TipoFactura.A);
        }else{
            factura.setTipo(TipoFactura.B);
        }

        //Generar número de factura secuencial
        String ultimoNumeroStr = facturaRepository.findUltimoNumeroPorTipo(factura.getTipo());
        long ultimoNumero = 0;
        if (ultimoNumeroStr != null) {
            try {
                ultimoNumero = Long.parseLong(ultimoNumeroStr);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear el último número de factura: " + ultimoNumeroStr);
            }
        }
        String nuevoNumero = String.format("%010d", ultimoNumero + 1);

        factura.setTotal(0f);
        for (Consumo c : consumos) {
            c.setFacturado(true);
            DetalleFactura detalle = new DetalleFactura();
            detalle.setDescripcion(c.getDescripcion());
            detalle.setCantidad(c.getCantidad());
            detalle.setPrecioUnitario(c.getMonto());
            detalle.setFactura(factura);
            detalle.calcularSubtotal();
            factura.agregarDetalleFactura(detalle);
        }
        
        factura.setNumero(nuevoNumero);
        factura.setResponsableDePago(responsable);
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setFecha(d.getFecha());
        factura.setEstadia(estadia);
        
        facturaRepository.save(factura);

        return generarPDFFactura(factura);
    }

    public byte[] generarPDFFactura(Factura factura) {
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

            // 2. Columna Central: Tipo de Factura
            PdfPCell cellCenter = new PdfPCell();
            cellCenter.setBorder(Rectangle.BOX);
            cellCenter.setVerticalAlignment(Element.ALIGN_TOP);
            
            Paragraph pOriginal = new Paragraph("ORIGINAL", fontSmall);
            pOriginal.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(pOriginal);
            
            Paragraph pLetra = new Paragraph(factura.getTipo().toString(), fontType);
            pLetra.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(pLetra);
            
            Paragraph pCod = new Paragraph("COD. " + (factura.getTipo() == TipoFactura.A ? "01" : "06"), fontSmall);
            pCod.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(pCod);
            
            headerTable.addCell(cellCenter);

            // 3. Columna Derecha: Datos Factura
            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(Rectangle.BOX);
            cellRight.setPadding(5);
            
            Paragraph pFacturaTitle = new Paragraph("FACTURA", fontTitle);
            pFacturaTitle.setAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(pFacturaTitle);
            
            cellRight.addElement(new Paragraph("Punto de Venta: 00007  Comp. Nro: " + factura.getNumero(), fontBold));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            cellRight.addElement(new Paragraph("Fecha de Emisión: " + sdf.format(java.sql.Date.valueOf(factura.getFecha())), fontBold));
            cellRight.addElement(new Paragraph("CUIT: 30-71093952-3", fontNormal));
            cellRight.addElement(new Paragraph("Ingresos Brutos: 30-71093952-3", fontNormal));
            cellRight.addElement(new Paragraph("Fecha de Inicio de Actividades: 01/11/2013", fontNormal));
            headerTable.addCell(cellRight);

            document.add(headerTable);

            // --- DATOS DEL CLIENTE ---
            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);
            clientTable.setWidths(new float[]{1, 3});
            clientTable.setSpacingBefore(5);

            ResponsablePago resp = factura.getResponsableDePago();
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

            PdfPCell cellCondVenta = new PdfPCell(new Phrase("Condición de venta: Contado", fontNormal));
            cellCondVenta.setColspan(2);
            cellCondVenta.setBorder(Rectangle.BOX);
            cellCondVenta.setPadding(3);
            clientTable.addCell(cellCondVenta);

            document.add(clientTable);
            document.add(new Paragraph("\n"));

            // --- DETALLE DE CONSUMOS ---
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

            for (DetalleFactura detalle : factura.getDetalles()) {
                double precioUnitario = detalle.getPrecioUnitario();
                double cantidad = detalle.getCantidad();
                double subtotal = detalle.getSubtotal();
                
                // Asumiendo que el precio guardado es final, desglosamos para mostrar estilo Factura A
                double subtotalNeto = subtotal / 1.21;
                double iva = subtotal - subtotalNeto;
                double precioUnitarioNeto = precioUnitario / 1.21;

                table.addCell(new Phrase("1", fontSmall));
                table.addCell(new Phrase(detalle.getDescripcion(), fontSmall));
                
                PdfPCell cellCant = new PdfPCell(new Phrase(String.valueOf(cantidad), fontSmall));
                cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellCant);
                
                table.addCell(new Phrase("Unid.", fontSmall));
                
                PdfPCell cellPrecio = new PdfPCell(new Phrase(String.format("%.2f", precioUnitarioNeto), fontSmall));
                cellPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellPrecio);
                
                table.addCell(new Phrase("0.00", fontSmall));
                
                PdfPCell cellSubNeto = new PdfPCell(new Phrase(String.format("%.2f", subtotalNeto), fontSmall));
                cellSubNeto.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellSubNeto);
                
                table.addCell(new Phrase("21%", fontSmall));
                
                PdfPCell cellSubTotal = new PdfPCell(new Phrase(String.format("%.2f", subtotal), fontSmall));
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
            cellFooterLeft.setBorder(Rectangle.NO_BORDER);
            cellFooterLeft.addElement(new Paragraph("Importe Otros Tributos: $ 0,00", fontNormal));
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

            // --- CAE ---
            document.add(new Paragraph("\n"));
            PdfPTable caeTable = new PdfPTable(1);
            caeTable.setWidthPercentage(100);
            PdfPCell caeCell = new PdfPCell();
            caeCell.setBorder(Rectangle.NO_BORDER);
            caeCell.addElement(new Paragraph("CAE: 12345678901234", fontBold));
            caeCell.addElement(new Paragraph("Vto. CAE: " + sdf.format(java.sql.Date.valueOf(factura.getFecha().plusDays(10))), fontBold));
            caeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            caeTable.addCell(caeCell);
            document.add(caeTable);

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