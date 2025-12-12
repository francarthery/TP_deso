package tp_hotel.tp_hotel.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaJuridicaDTO {
    @NotNull @Size(min = 1, max = 100) @Pattern(regexp = "^[a-zA-Z0-9ñÑÁÉÍÓÚÜáéíóúü.,&\\- ]+$")
    private String razonSocial;

    @NotNull @Size(min = 1, max = 13) @Pattern(regexp = "^[0-9]{2}-?[0-9]{8}-?[0-9]$")
    private String cuit;
    
    @NotNull @Size(min = 1, max = 20)
    private String telefono;

    private DireccionDTO direccion;

    public PersonaJuridica toEntity() {
        PersonaJuridica personaJuridica = new PersonaJuridica();
        personaJuridica.setRazonSocial(this.razonSocial);
        personaJuridica.setCuit(this.cuit);
        personaJuridica.setTelefono(this.telefono);
        if (this.direccion != null) {
            personaJuridica.setDireccion(this.direccion.toEntity());
        }
        return personaJuridica;
    }

    public PersonaJuridicaDTO(PersonaJuridica personaJuridica){
        this.razonSocial = personaJuridica.getRazonSocial();
        this.cuit = personaJuridica.getCUIT();
        this.telefono = personaJuridica.getTelefono();
        if (personaJuridica.getDireccion() != null) {
            this.direccion = new DireccionDTO(personaJuridica.getDireccion());
        }
    }
}