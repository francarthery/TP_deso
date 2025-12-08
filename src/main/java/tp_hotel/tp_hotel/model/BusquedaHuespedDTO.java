package tp_hotel.tp_hotel.model;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusquedaHuespedDTO {
    @Pattern(regexp = "^[a-zA-Z ]+$") @Size(min = 1, max = 50)
    private String apellido;
    
    @Pattern(regexp = "^[a-zA-Z ]+$") @Size(min = 1, max = 50)
    private String nombres;
    
    private TipoDocumento tipoDocumento;
    
    @Size(min = 1, max = 10)
    private String numeroDocumento;
}
