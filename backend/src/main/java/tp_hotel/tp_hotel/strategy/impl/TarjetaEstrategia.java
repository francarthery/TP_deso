package tp_hotel.tp_hotel.strategy.impl;

import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;
import tp_hotel.tp_hotel.model.PagoTarjetaDebito;
public class TarjetaEstrategia implements EstrategiaPago{
    
    //Las validaciones de formato se realizan en tipoPagoDTO
    @Override
    public boolean validar(TipoPago tipoPago) {
        PagoTarjetaDebito tarjetaDebito = (PagoTarjetaDebito) tipoPago;
        
        if(tarjetaDebito.getNumeroTarjeta() == null || tarjetaDebito.getNombreTitular() == null ||
         tarjetaDebito.getFechaVencimiento() == null || tarjetaDebito.getCodigoSeguridad() == null){
            return false;
        }

        if(!esNumeroLuhnValido(tarjetaDebito.getNumeroTarjeta())){
            return false;
        }

        return true;
    }

    private boolean esNumeroLuhnValido(String numeroTarjeta) {
        String numeroLimpio = numeroTarjeta.replaceAll("\\s+|-", "");

        if (!numeroLimpio.matches("\\d+")) {
            return false;
        }

        int suma = 0;
        boolean alternar = false;

        for (int i = numeroLimpio.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numeroLimpio.substring(i, i + 1));

            if (alternar) {
                n *= 2;
                if (n > 9) {
                    n -= 9; 
                }
            }
            
            suma += n;
            alternar = !alternar;
        }

        return (suma % 10 == 0);
    }

}
