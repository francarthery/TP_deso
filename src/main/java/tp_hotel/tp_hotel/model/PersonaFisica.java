package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id") 
public class PersonaFisica extends ResponsablePago {
    @OneToOne
    @JoinColumn(name = "huesped_id")
    private Huesped huesped;
}