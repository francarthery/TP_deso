package tp_hotel.tp_hotel.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PagoMonedaExtranjera extends TipoPago {
    private String moneda;
    private Float cotizacion;  
}