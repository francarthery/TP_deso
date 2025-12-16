package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFacturaDTO {
    private LocalDate fecha;
    private Integer idEstadia;
    private List<Integer> idConsumos;
    private Integer idResponsablePago;
}