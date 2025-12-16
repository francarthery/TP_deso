package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadiaDTO {

    private Integer id;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;
    
    @NotNull @Size(min = 1, max = 2)
    private String numeroHabitacion;

    @NotNull
    private Integer idHuespedTitular;
    private List<Integer> idsHuespedesInvitados;

    private List<Integer> idsConsumos = new ArrayList<>();

    public EstadiaDTO(Estadia estadia) {
        this.id = estadia.getId();
        this.checkIn = estadia.getCheckIn();
        this.checkOut = estadia.getCheckOut();
        if (estadia.getHabitacion() != null) {
            this.numeroHabitacion = estadia.getHabitacion().getNumero();
        }
        if (estadia.getHuespedTitular() != null) {
            this.idHuespedTitular = estadia.getHuespedTitular().getId();
        }
        if (estadia.getHuespedInvitados() != null) {
            this.idsHuespedesInvitados = estadia.getHuespedInvitados().stream()
                .map(Huesped::getId)
                .toList();
        }
        if (estadia.getConsumos() != null) {
            this.idsConsumos = estadia.getConsumos().stream()
                .map(Consumo::getId)
                .toList();
        }
    }
}