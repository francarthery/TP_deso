package tp_hotel.tp_hotel.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionEstadoDTO {
    private String numero;
    private CategoriaHabitacion categoria;
    private List<EstadoDiaDTO> estadosPorDia;
}
