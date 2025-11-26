package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoDTO {
    private Integer id;
    private String descripcion;
    private Float monto;
    private LocalDate fecha;
    private Integer idEstadia;
}