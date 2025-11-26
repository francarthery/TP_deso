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
    
    // IDs para creación
    private String numeroHabitacion; // Ahora es el ID
    private Integer idHuesped;
    private Integer idReserva; // Opcional, si viene de una reserva
    
    // Datos informativos
    // private String numeroHabitacion; // Ya está arriba
    private String nombreHuesped;
    private String apellidoHuesped;
    
    private List<ConsumoDTO> consumos = new ArrayList<>();
    // Evitamos anidar entidades completas como Reserva o Factura para no ciclar
}