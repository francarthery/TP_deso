package tp_hotel.tp_hotel.model;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PagoCheque extends TipoPago {
    private String numero;
    private String banco;
    private String plaza;
    private LocalDate fechaCobro;
}