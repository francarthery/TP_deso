package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadiaDTO {
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer idHabitacion;  
    private String huespedTitular; 
    private Integer cantidadAcompanantes;
}