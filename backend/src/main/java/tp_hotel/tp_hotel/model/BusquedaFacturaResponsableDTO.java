package tp_hotel.tp_hotel.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusquedaFacturaResponsableDTO{
    @Size(min = 1, max = 13)
    @Pattern(regexp = "^[0-9]{2}-?[0-9]{8}-?[0-9]$")
    String cuit;
    
    TipoDocumento tipoDocumento;
    
    @Pattern(regexp = "^[0-9A-Z]+$")
    String numeroDocumento;
}