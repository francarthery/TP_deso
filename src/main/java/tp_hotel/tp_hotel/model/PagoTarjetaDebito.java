package tp_hotel.tp_hotel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class PagoTarjetaDebito extends TipoPago {
    private String tarjeta;   
    private String nroAutorizacion;
}