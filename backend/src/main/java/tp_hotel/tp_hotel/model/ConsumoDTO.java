package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoDTO {
    private Integer id;
    private String descripcion;
    private Float monto;
    private LocalDate fecha;
    private Integer idEstadia;
    private Integer cantidad;
    private boolean facturado;
    
    public ConsumoDTO(Consumo consumo) {
        this.id = consumo.getId();
        this.descripcion = consumo.getDescripcion();
        this.monto = consumo.getMonto();
        this.fecha = consumo.getFecha();
        this.cantidad = consumo.getCantidad();
        this.facturado = consumo.isFacturado();
        if (consumo.getEstadia() != null) {
            this.idEstadia = consumo.getEstadia().getId();
        }
    }
}