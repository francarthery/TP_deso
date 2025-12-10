package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonaJuridica extends ResponsablePago {
    
    @Column(length = 150, nullable = false)
    private String razonSocial;

    @Column(length = 11, nullable = false)
    private String cuit;

    @Column(length = 20, nullable = false)
    private String telefono;
    
    @Embedded
    private Direccion direccion;

    public String getRazonSocial(){
        return this.razonSocial;
    }
    public String getCUIT(){
        return this.cuit;
    }
    public Direccion getDireccion(){
        return this.direccion;
    }
    public String getTelefono(){
        return this.telefono;
    }
}