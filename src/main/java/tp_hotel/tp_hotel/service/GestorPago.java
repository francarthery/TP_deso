package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.Pago;
import tp_hotel.tp_hotel.model.PagoMoneda;
import tp_hotel.tp_hotel.model.PagoTarjetaCredito;
import tp_hotel.tp_hotel.model.PagoTarjetaDebito;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.model.TipoPagoDTO;
import tp_hotel.tp_hotel.repository.FacturaRepository;
import tp_hotel.tp_hotel.repository.TipoPagoRepository;
import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.factory.TipoPagoFactory;

@Service
public class GestorPago{

    private final TipoPagoRepository tipoPagoRepository;
    private final GestorFacturacion gestorFacturacion;
    
    @Autowired
    public GestorPago(GestorFacturacion gestorFacturacion, TipoPagoRepository tipoPagoRepository) {
        this.gestorFacturacion = gestorFacturacion;
        this.tipoPagoRepository = tipoPagoRepository;
    }

    public void registrarPago(Factura f, TipoPago p) {
    }

    public List<Factura> obtenerFacturasPendientes(int habitacion) {
        return null;
    }

    public Float calcularVuelto(Float total, List<TipoPago> pagos) {
        return 0.0f;
    }

    @Transactional
    public Integer ingresarPago(List<TipoPagoDTO> tipoPagoEntrante, Integer facturaId) {
        List<TipoPago> tiposPago = tipoPagoEntrante.stream().map(TipoPagoFactory::crearTipoPago).toList();
        Factura factura = gestorFacturacion.obtenerFacturaPorId(facturaId);
        factura.setEstado(EstadoFactura.PAGADA);
        
        Pago pago = new Pago();
        pago.setFecha(LocalDate.now());
        pago.setFactura(factura);
        pago.setFormasDePago(tiposPago);

        
        
        for(TipoPago tp : tiposPago){
            if(tp instanceof PagoMoneda){

            }else if(tp instanceof PagoTarjetaCredito){

            }else if(tp instanceof PagoTarjetaDebito){
                
            }else{
                
            }
            
        }

        return pago.getId();
    }
}