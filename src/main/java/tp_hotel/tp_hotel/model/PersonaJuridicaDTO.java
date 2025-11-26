package tp_hotel.tp_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaJuridicaDTO {
    private String razonSocial;
    private String cuit;
    private DireccionDTO direccion;
}