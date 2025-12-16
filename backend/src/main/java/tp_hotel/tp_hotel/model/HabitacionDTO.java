package tp_hotel.tp_hotel.model; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionDTO {
    private String numero;
    private CategoriaHabitacion categoria;
    private Float costoNoche;
    private EstadoHabitacion estadoActual;
}