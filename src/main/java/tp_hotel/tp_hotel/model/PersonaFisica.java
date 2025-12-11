package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonaFisica extends ResponsablePago {
    
    @OneToOne
    @JoinColumn(name = "huesped_id")
    private Huesped huesped;

    public String getRazonSocial(){
        return this.huesped.getApellido() + " " + this.huesped.getNombres();
    }
    public String getCUIT(){
        return this.huesped.getCuit();
    }
    public Direccion getDireccion(){
        return this.huesped.getDireccion();
    }
    public String getTelefono(){
        return this.huesped.getTelefono();
    }
    
}