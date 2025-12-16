package tp_hotel.tp_hotel.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusquedaResponsablePagoDTO {
    @Size(min = 1, max = 13)
    @Pattern(regexp = "^[0-9]{2}-?[0-9]{8}-?[0-9]$")
    private String cuit;
    @Size(min = 1, max = 100) @Pattern(regexp = "^[a-zA-Z0-9ñÑÁÉÍÓÚÜáéíóúü.,&\\- ]+$")
    private String razonSocial;
}
