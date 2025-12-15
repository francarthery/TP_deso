package tp_hotel.tp_hotel.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Integer id;
    private String numero;
    private LocalDate fecha;
    private TipoFactura tipo;
    private String razonSocial;
    private float total;
    
    public FacturaDTO(Factura factura){
        this.id = factura.getId();
        this.numero = factura.getNumero();
        this.fecha = factura.getFecha();
        this.tipo = factura.getTipo();
        this.total = factura.getTotal();
        this.razonSocial = factura.getResponsableDePago().getRazonSocial();
    }
}
