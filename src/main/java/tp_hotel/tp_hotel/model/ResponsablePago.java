package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
  
    public abstract String getRazonSocial();
    public abstract String getCUIT();
    public abstract Direccion getDireccion();
    public abstract String getTelefono();
}