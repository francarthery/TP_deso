package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Integer id;
    private String numero;
    private LocalDate fecha;
    private TipoFactura tipo;
    private EstadoFactura estado;
    private Float total;
    private String responsableCUIT;
}