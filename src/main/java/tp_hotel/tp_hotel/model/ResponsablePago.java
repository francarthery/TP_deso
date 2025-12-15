package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 150, nullable = false)
    private String razonSocial;
  
    public abstract String getCUIT();
    public abstract Direccion getDireccion();
    public abstract String getTelefono();
}