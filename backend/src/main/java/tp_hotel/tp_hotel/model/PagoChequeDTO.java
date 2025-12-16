package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoChequeDTO {
    private String numero;
    private String banco;
    private String plaza;
    private LocalDate fechaCobro;
}