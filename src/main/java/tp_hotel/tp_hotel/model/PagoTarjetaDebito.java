package tp_hotel.tp_hotel.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class PagoTarjetaDebito extends TipoPago {
    private String numeroTarjeta;
    private String nombreTitular;
    private LocalDate fechaVencimiento;
    private String codigoSeguridad;
}