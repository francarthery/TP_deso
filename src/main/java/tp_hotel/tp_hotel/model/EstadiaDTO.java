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

    private Integer idHuespedTitular;
    private List<Integer> idsHuespedesInvitados;

    private List<ConsumoDTO> consumos = new ArrayList<>();

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
        
    }
}