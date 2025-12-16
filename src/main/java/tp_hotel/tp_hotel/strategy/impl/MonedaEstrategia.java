package tp_hotel.tp_hotel.strategy.impl;
import tp_hotel.tp_hotel.model.PagoMoneda;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;
import tp_hotel.tp_hotel.model.TipoPago;

public class MonedaEstrategia implements EstrategiaPago {
    
    //Las validaciones de formato se realizan en tipoPagoDTO
    @Override
    public boolean validar(TipoPago tipoPago) {
        PagoMoneda pagoMoneda = (PagoMoneda) tipoPago;
        
        if(pagoMoneda.getMoneda() == null || 
       /*  pagoMoneda.getCotizacion() == null || */ pagoMoneda.getImporte() == null){
            return false;
        }

        return true;
    }
}

