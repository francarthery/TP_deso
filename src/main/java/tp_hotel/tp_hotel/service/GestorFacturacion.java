package tp_hotel.tp_hotel.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

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

import tp_hotel.tp_hotel.model.Consumo;
import tp_hotel.tp_hotel.model.DatosFacturaDTO;
import tp_hotel.tp_hotel.model.DetalleFactura;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.NotaCredito;
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
        List<DetalleFactura> detalles = consumos.stream().map(c -> {
            c.setFacturado(true);
            DetalleFactura detalle = new DetalleFactura();
            detalle.setDescripcion(c.getDescripcion());
            detalle.setCantidad(c.getCantidad());
            detalle.setPrecioUnitario(c.getMonto());
            detalle.setFactura(factura);
            detalle.calcularSubtotal();
            factura.agregarDetalleFactura(detalle);
            return detalle;
        }).toList();
        
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
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fuentes
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 8);

            // --- CABECERA ---
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{4, 1, 4}); // Columnas: Izq, Centro (Letra), Der

            // Columna Izquierda: Datos del Hotel (Emisor)
            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(Rectangle.NO_BORDER);
            cellLeft.addElement(new Paragraph("HOTEL TP SOFTWARE", fontTitle));
            cellLeft.addElement(new Paragraph("Razón Social: Hotel S.A.", fontBold));
            cellLeft.addElement(new Paragraph("Domicilio: Calle Falsa 123, CABA", fontNormal));
            cellLeft.addElement(new Paragraph("Condición frente al IVA: Responsable Inscripto", fontSmall));
            headerTable.addCell(cellLeft);

            // Columna Central: Tipo de Factura (A o B)
            PdfPCell cellCenter = new PdfPCell();
            cellCenter.setBorder(Rectangle.BOX);
            cellCenter.setBackgroundColor(Color.LIGHT_GRAY);
            cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellCenter.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellCenter.addElement(new Paragraph(factura.getTipo().toString(), fontTitle));
            cellCenter.addElement(new Paragraph("COD. " + (factura.getTipo() == TipoFactura.A ? "01" : "06"), fontSmall));
            headerTable.addCell(cellCenter);

            // Columna Derecha: Datos de la Factura
            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(Rectangle.NO_BORDER);
            cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellRight.addElement(new Paragraph("FACTURA", fontTitle));
            cellRight.addElement(new Paragraph("Nro: " + factura.getNumero(), fontBold));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            cellRight.addElement(new Paragraph("Fecha: " + sdf.format(java.sql.Date.valueOf(factura.getFecha())), fontNormal));
            cellRight.addElement(new Paragraph("CUIT: 30-12345678-9", fontNormal));
            cellRight.addElement(new Paragraph("Ingresos Brutos: 123456", fontNormal));
            cellRight.addElement(new Paragraph("Inicio de Actividades: 01/01/2020", fontNormal));
            headerTable.addCell(cellRight);

            document.add(headerTable);
            document.add(new Paragraph("\n")); // Espacio

            // --- DATOS DEL CLIENTE ---
            PdfPTable clientTable = new PdfPTable(1);
            clientTable.setWidthPercentage(100);
            PdfPCell clientCell = new PdfPCell();
            clientCell.setBorder(Rectangle.BOX);
            
            ResponsablePago resp = factura.getResponsableDePago();
            String nombreCliente = "";
            String cuitCliente = "";
            String direccionCliente = "";
            String condicionIva = "Consumidor Final";

            if (resp instanceof PersonaFisica) {
                PersonaFisica pf = (PersonaFisica) resp;
                nombreCliente = pf.getHuesped().getNombres() + " " + pf.getHuesped().getApellido();
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

            clientCell.addElement(new Paragraph("Señor(es): " + nombreCliente, fontNormal));
            clientCell.addElement(new Paragraph("Domicilio: " + direccionCliente, fontNormal));
            clientCell.addElement(new Paragraph("CUIT/DNI: " + cuitCliente + "   Condición IVA: " + condicionIva, fontNormal));
            clientTable.addCell(clientCell);

            document.add(clientTable);
            document.add(new Paragraph("\n"));

            // --- DETALLE DE CONSUMOS ---
            PdfPTable table = new PdfPTable(4); // Concepto, Cantidad, Precio U., Subtotal
            table.setWidthPercentage(100);
            table.setWidths(new float[]{5, 1, 2, 2});

            // Encabezados de tabla
            String[] headers = {"Concepto / Descripción", "Cant.", "Precio Unit.", "Subtotal"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontBold));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Filas de consumos
            for (DetalleFactura detalle : factura.getDetalles()) {
                table.addCell(new Phrase(detalle.getDescripcion(), fontNormal));
                
                PdfPCell cellCant = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), fontNormal));
                cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellCant);

                PdfPCell cellPrecio = new PdfPCell(new Phrase("$ " + String.format("%.2f", detalle.getPrecioUnitario()), fontNormal));
                cellPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellPrecio);

                PdfPCell cellSub = new PdfPCell(new Phrase("$ " + String.format("%.2f", detalle.getSubtotal()), fontNormal));
                cellSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellSub);
            }
            
            document.add(table);

            // --- TOTALES ---
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{8, 2});

            PdfPCell cellTotalLabel = new PdfPCell(new Phrase("TOTAL", fontTitle));
            cellTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalLabel.setBorder(Rectangle.NO_BORDER);
            totalTable.addCell(cellTotalLabel);

            PdfPCell cellTotalValue = new PdfPCell(new Phrase("$ " + String.format("%.2f", factura.getTotal()), fontTitle));
            cellTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTotalValue.setBorder(Rectangle.BOX);
            totalTable.addCell(cellTotalValue);

            document.add(totalTable);

            // --- PIE DE PAGINA (CAE) ---
            document.add(new Paragraph("\n"));
            Paragraph cae = new Paragraph("CAE: 12345678901234   |   Vto. CAE: " + sdf.format(java.sql.Date.valueOf(factura.getFecha().plusDays(10))), fontBold);
            cae.setAlignment(Element.ALIGN_RIGHT);
            document.add(cae);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}