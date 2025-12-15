package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.NotaCreditoNoExistenteException;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.NotaCredito;
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

        // Lógica para generar el reporte en formato PDF
        return new byte[0];
    }
}
