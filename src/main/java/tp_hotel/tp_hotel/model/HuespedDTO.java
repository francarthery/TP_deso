package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import tp_hotel.tp_hotel.model.TipoDocumento;
import tp_hotel.tp_hotel.model.IVA; 

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuespedDTO {
    private String apellido;
    private String nombres;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String cuit;
    private IVA posicionIVA;
    private String nacionalidad;
    private String telefono;
    private String email;
    private DireccionDTO direccion;
}