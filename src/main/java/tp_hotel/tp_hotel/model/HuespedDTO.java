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

    public HuespedDTO(Huesped huesped) {
        this.apellido = huesped.getApellido();
        this.nombres = huesped.getNombres();
        this.tipoDocumento = huesped.getTipoDocumento();
        this.numeroDocumento = huesped.getNumeroDocumento();
        this.fechaNacimiento = huesped.getFechaDeNacimiento();
        this.cuit = huesped.getCuit();
        this.posicionIVA = huesped.getPosicionFrenteAlIVA();
        this.nacionalidad = huesped.getNacionalidad();
        this.telefono = huesped.getTelefono();
        this.email = huesped.getEmail();
        if (huesped.getDireccion() != null) {
            this.direccion = new DireccionDTO(huesped.getDireccion());
        }
    }

    public Huesped toEntity() {
        Huesped huesped = new Huesped();
        huesped.setApellido(this.apellido);
        huesped.setNombres(this.nombres);
        huesped.setTipoDocumento(this.tipoDocumento);
        huesped.setNumeroDocumento(this.numeroDocumento);
        huesped.setFechaDeNacimiento(this.fechaNacimiento);
        huesped.setCuit(this.cuit);
        huesped.setPosicionFrenteAlIVA(this.posicionIVA);
        huesped.setNacionalidad(this.nacionalidad);
        huesped.setTelefono(this.telefono);
        huesped.setEmail(this.email);
        if (this.direccion != null) {
            huesped.setDireccion(this.direccion.toEntity());
        }
        return huesped;
    }
}