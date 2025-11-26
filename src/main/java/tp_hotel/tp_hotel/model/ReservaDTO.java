package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Integer id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoReserva estado;
    private LocalDateTime fechaCreacion;
    
    // Datos para creación/referencia
    private String numeroHabitacion; // Ahora es el ID
    private Integer idHuesped;
    
    // Datos informativos (opcionales en creación)
    private String nombreHuesped;
    private String apellidoHuesped;
    private String telefonoHuesped;
    // private String numeroHabitacion; // Ya está arriba como ID
}