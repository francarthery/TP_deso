package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id")
public class PersonaJuridica extends ResponsablePago {

    @Column(length = 50)
    private String razonSocial;

    @Column(length = 20)
    private String cuit;

    @Embedded
    private Direccion direccion;
}