package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {
    private String pais;
    private String provincia;
    private String localidad;
    private String calle;
    private String numero; 
    private String piso;
    private String departamento;
    private String codigoPostal;
    
    public String getDireccionCompleta() {
        return this.calle + " " + this.numero + ", " + this.localidad;
    }
}