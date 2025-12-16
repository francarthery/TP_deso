package tp_hotel.tp_hotel.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoDiaDTO {
    private LocalDate fecha;
    private EstadoHabitacion estado;
    private Integer idReferencia;
}
