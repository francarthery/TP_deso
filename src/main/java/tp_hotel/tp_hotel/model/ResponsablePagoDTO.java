package tp_hotel.tp_hotel.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsablePagoDTO {
    
    private Integer id;
    private String razonSocial;
    private String cuit;
    
    public ResponsablePagoDTO(ResponsablePago responsablePago){
        this.id = responsablePago.getId();
        this.razonSocial = responsablePago.getRazonSocial();
        this.cuit = responsablePago.getCUIT();
    }
}