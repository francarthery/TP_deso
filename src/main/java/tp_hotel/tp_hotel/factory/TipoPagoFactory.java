package tp_hotel.tp_hotel.factory;

import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.model.TipoPagoDTO;

import tp_hotel.tp_hotel.model.MetodoPago;
import tp_hotel.tp_hotel.model.PagoCheque;
import tp_hotel.tp_hotel.model.PagoMoneda;
import tp_hotel.tp_hotel.model.PagoTarjetaCredito;
import tp_hotel.tp_hotel.model.PagoTarjetaDebito;

public class TipoPagoFactory {
    public static TipoPago crearTipoPago(TipoPagoDTO tipoPagoDto) {
        TipoPago tipoPago = null;
        
        switch(tipoPagoDto.getMetodoPago()){
            case MONEDA:
                PagoMoneda moneda = new PagoMoneda();
                moneda.setCotizacion(tipoPagoDto.getCotizacion());
                moneda.setMoneda(tipoPagoDto.getTipoMoneda());
                moneda.setMetodoPago(MetodoPago.MONEDA);
                tipoPago = moneda;
                break;
            case TARJETA_CREDITO:
                PagoTarjetaCredito tarjetaCredito = new PagoTarjetaCredito();
                tarjetaCredito.setNumeroTarjeta(tipoPagoDto.getNumeroTarjeta());
                tarjetaCredito.setNombreTitular(tipoPagoDto.getTitular());
                tarjetaCredito.setCodigoSeguridad(tipoPagoDto.getCodigoSeguridad());
                tarjetaCredito.setFechaVencimiento(tipoPagoDto.getFechaVencimiento());
                tarjetaCredito.setMetodoPago(MetodoPago.TARJETA_CREDITO);
                tipoPago = tarjetaCredito;
                break;
            case TARJETA_DEBITO:
                PagoTarjetaDebito tarjetaDebito = new PagoTarjetaDebito();
                tarjetaDebito.setNumeroTarjeta(tipoPagoDto.getNumeroTarjeta());
                tarjetaDebito.setNombreTitular(tipoPagoDto.getTitular());
                tarjetaDebito.setCodigoSeguridad(tipoPagoDto.getCodigoSeguridad());
                tarjetaDebito.setFechaVencimiento(tipoPagoDto.getFechaVencimiento());
                tarjetaDebito.setMetodoPago(MetodoPago.TARJETA_DEBITO);
                tipoPago = tarjetaDebito;
                break;
            case CHEQUE:
                PagoCheque cheque = new PagoCheque();
                cheque.setNumero(tipoPagoDto.getNumeroCheque());
                cheque.setBanco(tipoPagoDto.getBancoEmisor());
                cheque.setPlaza(tipoPagoDto.getPlaza());
                cheque.setFechaCobro(tipoPagoDto.getFechaCobro());
                cheque.setMetodoPago(MetodoPago.CHEQUE);
                tipoPago = cheque;
                break;
        }
        
        tipoPago.setImporte(tipoPagoDto.getImporte());
        
        return tipoPago;
    }
}
