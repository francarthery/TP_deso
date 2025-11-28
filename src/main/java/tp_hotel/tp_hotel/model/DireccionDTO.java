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