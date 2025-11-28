package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadiaDTO {

    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    
    private String numeroHabitacion;
    private Integer idHuesped;
    private Integer idReserva;

    private String nombreHuesped;
    private String apellidoHuesped;

    private List<ConsumoDTO> consumos = new ArrayList<>();
}