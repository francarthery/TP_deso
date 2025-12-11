package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuespedDTO {
    private Integer id;
    
    @Pattern(regexp = "^[a-zA-Z ]+$") @Size(min = 1, max = 50) @NotNull
    private String apellido;
    
    @Pattern(regexp = "^[a-zA-Z ]+$") @Size(min = 1, max = 50) @NotNull
    private String nombres;

    @NotNull
    private TipoDocumento tipoDocumento;

    @Size(min = 1, max = 10) @NotNull
    private String numeroDocumento;

    @Past @NotNull
    private LocalDate fechaDeNacimiento;

    @Size(min = 1, max = 13)
    @Pattern(regexp = "^[0-9]{2}-?[0-9]{8}-?[0-9]$")
    private String cuit;

    @NotNull
    private IVA posicionFrenteAlIVA;

    @NotNull @Size(min = 1, max = 85)
    private String nacionalidad;

    @NotNull @Size(min = 1, max = 20)
    private String telefono;
    
    @Email 
    private String email;

    @NotNull @Size(min = 1, max = 50)
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