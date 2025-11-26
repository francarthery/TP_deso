package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Crea tablas separadas para Física y Jurídica
@Data
public abstract class ResponsablePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}