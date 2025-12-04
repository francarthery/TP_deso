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
    private String numeroHabitacion; 
    private String nombreHuesped;
    private String apellidoHuesped;
    private String telefonoHuesped;
    private CategoriaHabitacion categoriaHabitacion;

    public ReservaDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.fechaInicio = reserva.getFechaInicio();
        this.fechaFin = reserva.getFechaFin();
        this.estado = reserva.getEstado();
        this.fechaCreacion = reserva.getFechaCreacion();
        if (reserva.getHabitacion() != null) {
            this.numeroHabitacion = reserva.getHabitacion().getNumero();
            this.categoriaHabitacion = reserva.getHabitacion().getCategoria();
        }
        this.nombreHuesped = reserva.getNombreHuesped();
        this.apellidoHuesped = reserva.getApellidoHuesped();
        this.telefonoHuesped = reserva.getTelefonoHuesped();

    }
}