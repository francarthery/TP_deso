package tp_hotel.tp_hotel.strategy.impl;

import tp_hotel.tp_hotel.model.PagoTarjetaCredito;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;

public class TarjetaCreditoEstrategia implements EstrategiaPago{
    //Las validaciones de formato se realizan en tipoPagoDTO
    @Override
    public boolean validar(TipoPago tipoPago) {
        PagoTarjetaCredito tarjeta = (PagoTarjetaCredito) tipoPago;
        
        if(tarjeta.getNumeroTarjeta() == null || tarjeta.getNombreTitular() == null ||
         tarjeta.getFechaVencimiento() == null || tarjeta.getCodigoSeguridad() == null){
            return false;
        }

        if(!ValidadorTarjeta.esNumeroLuhnValido(tarjeta.getNumeroTarjeta())){
            return false;
        }

        return true;
    }
}
