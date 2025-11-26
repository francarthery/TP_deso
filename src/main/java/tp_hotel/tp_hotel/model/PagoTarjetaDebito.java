package tp_hotel.tp_hotel.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PagoTarjetaDebito extends TipoPago {
    private String tarjeta;   
    private String nroAutorizacion;
}