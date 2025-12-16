package tp_hotel.tp_hotel.strategy.impl;

import java.time.LocalDate;

import tp_hotel.tp_hotel.model.PagoCheque;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;

public class ChequeEstrategia implements EstrategiaPago{
    //Las validaciones de formato se realizan en tipoPagoDTO
    @Override
    public boolean validar(TipoPago tipoPago){
        PagoCheque cheque = (PagoCheque) tipoPago;

        if(cheque.getNumero() == null || cheque.getBanco() == null ||
         cheque.getFechaCobro() == null || cheque.getPlaza() == null || cheque.getImporte() == null){
            return false;
        }

        return true;
    }
}
