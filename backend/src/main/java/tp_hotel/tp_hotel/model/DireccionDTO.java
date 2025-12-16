package tp_hotel.tp_hotel.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$") @Size(min = 1, max = 30)
    private String pais; 
    
    @Size(min = 1, max = 50)
    private String provincia;
    
    @Size(min = 1, max = 50)
    private String localidad;

    @NotNull @Size(min = 1, max = 100)
    private String calle;

    @NotNull @Pattern(regexp = "^[0-9]+$") @Size(min = 1, max = 10)
    private String numero; 

    @Size(min = 1, max = 4)
    private String piso;

    @Size(min = 1, max = 5)
    private String departamento;

    @NotNull @Size(min = 1, max = 10)
    private String codigoPostal;
    
    public String getDireccionCompleta() {
        return this.calle + " " + this.numero + ", " + this.localidad;
    }

    public DireccionDTO(Direccion direccion) {
        if (direccion != null) {
            this.pais = direccion.getPais();
            this.provincia = direccion.getProvincia();
            this.localidad = direccion.getLocalidad();
            this.calle = direccion.getCalle();
            this.numero = direccion.getNumero();
            this.piso = direccion.getPiso();
            this.departamento = direccion.getDepartamento();
            this.codigoPostal = direccion.getCodigoPostal();
        }
    }

    public Direccion toEntity() {
        Direccion direccion = new Direccion();
        direccion.setPais(this.pais);
        direccion.setProvincia(this.provincia);
        direccion.setLocalidad(this.localidad);
        direccion.setCalle(this.calle);
        direccion.setNumero(this.numero);
        direccion.setPiso(this.piso);
        direccion.setDepartamento(this.departamento);
        direccion.setCodigoPostal(this.codigoPostal);
        return direccion;
    }
}