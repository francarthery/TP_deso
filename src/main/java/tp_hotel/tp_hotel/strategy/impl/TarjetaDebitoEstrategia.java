package tp_hotel.tp_hotel.strategy.impl;

import tp_hotel.tp_hotel.model.PagoTarjetaDebito;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;

public class TarjetaDebitoEstrategia implements EstrategiaPago{
    //Las validaciones de formato se realizan en tipoPagoDTO
    @Override
    public boolean validar(TipoPago tipoPago) {
        PagoTarjetaDebito tarjeta = (PagoTarjetaDebito) tipoPago;
        
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
