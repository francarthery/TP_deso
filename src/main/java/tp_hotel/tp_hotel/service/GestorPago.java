package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy;
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
import tp_hotel.tp_hotel.repository.PagoRepository;
import tp_hotel.tp_hotel.strategy.StrategyFactory;
import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.PagoInsuficienteException;
import tp_hotel.tp_hotel.exceptions.TipoPagoIncorrectoException;
import tp_hotel.tp_hotel.factory.TipoPagoFactory;

@Service
public class GestorPago{

    private final PagoRepository pagoRepository;
    private final GestorFacturacion gestorFacturacion;
    
    @Autowired
    public GestorPago(GestorFacturacion gestorFacturacion, PagoRepository pagoRepository) {
        this.gestorFacturacion = gestorFacturacion;
        this.pagoRepository = pagoRepository;
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
        for(TipoPago tp : tiposPago){
            boolean validacionCorrecta = StrategyFactory.getStrategy(tp.getMetodoPago()).validar(tp);
            if(!validacionCorrecta){
                throw new TipoPagoIncorrectoException("El tipo de pago ingresado es incorrecto.");
            }
        }
        
        Factura factura = gestorFacturacion.obtenerFacturaPorId(facturaId);
        
        Pago pago = new Pago();
        pago.setFecha(LocalDate.now());
        pago.setFactura(factura);
        pago.setFormasDePago(tiposPago);
        pago.calcularMontoTotal();
        
        if(pago.getMontoTotal() < factura.getTotal()){
            throw new PagoInsuficienteException("El monto del pago " + pago.getMontoTotal() + 
            ", es insuficiente para pagar el importe de la factura " + factura.getTotal());
        }
        factura.setEstado(EstadoFactura.PAGADA);
        pagoRepository.save(pago);

        return pago.getId();
    }
}