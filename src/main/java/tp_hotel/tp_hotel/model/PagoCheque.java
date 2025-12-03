package tp_hotel.tp_hotel.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class PagoCheque extends TipoPago {
    @Column(length = 50, nullable = false)
    private String numero;
    @Column(length = 100, nullable = false)
    private String banco;
    @Column(length = 100)
    private String plaza;
    private LocalDate fechaCobro;
}