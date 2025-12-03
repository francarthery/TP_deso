package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuespedDTO {
    private Integer id;
    private String apellido;
    private String nombres;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaDeNacimiento;
    private String cuit;
    private IVA posicionFrenteAlIVA;
    private String nacionalidad;
    private String telefono;
    private String email;
    private String ocupacion;
    private DireccionDTO direccion;

    public HuespedDTO(Huesped huesped) {
        this.id = huesped.getId();
        this.apellido = huesped.getApellido();
        this.nombres = huesped.getNombres();
        this.tipoDocumento = huesped.getTipoDocumento();
        this.numeroDocumento = huesped.getNumeroDocumento();
        this.fechaDeNacimiento = huesped.getFechaDeNacimiento();
        this.cuit = huesped.getCuit();
        this.posicionFrenteAlIVA = huesped.getPosicionFrenteAlIVA();
        this.nacionalidad = huesped.getNacionalidad();
        this.telefono = huesped.getTelefono();
        this.email = huesped.getEmail();
        this.ocupacion = huesped.getOcupacion();
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
        huesped.setFechaDeNacimiento(this.fechaDeNacimiento);
        huesped.setCuit(this.cuit);
        huesped.setPosicionFrenteAlIVA(this.posicionFrenteAlIVA);
        huesped.setNacionalidad(this.nacionalidad);
        huesped.setTelefono(this.telefono);
        huesped.setEmail(this.email);
        huesped.setOcupacion(this.ocupacion);
        if (this.direccion != null) {
            huesped.setDireccion(this.direccion.toEntity());
        }
        return huesped;
    }
}