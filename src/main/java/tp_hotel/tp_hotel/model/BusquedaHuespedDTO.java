package tp_hotel.tp_hotel.model;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusquedaHuespedDTO {
    @Size(min = 1, max = 50)
    private String apellido;
    
    @Size(min = 1, max = 50)
    private String nombres;
    
    private TipoDocumento tipoDocumento;
    
    @Pattern(regexp = "^[0-9A-Z]+$") @Size(min = 1, max = 10)
    private String numeroDocumento;
}
