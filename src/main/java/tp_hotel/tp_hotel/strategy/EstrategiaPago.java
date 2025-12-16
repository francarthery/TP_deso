package tp_hotel.tp_hotel.strategy;

import tp_hotel.tp_hotel.model.TipoPago;

public interface EstrategiaPago {
    boolean validar(TipoPago tipoPago);
    //Float obtenerMonto(TipoPago tipoPago);   
}