package tp_hotel.tp_hotel.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String razonSocial;

    @OneToMany(mappedBy = "responsablePago", cascade = CascadeType.ALL)
    List<Factura> facturas;
    
    public abstract String getCUIT();
    public abstract Direccion getDireccion();
    public abstract String getTelefono();
	
    public void asociarFactura(Factura factura) {
		facturas.add(factura);
	}

    public boolean tieneFacturas(){
        return !facturas.isEmpty();
    }
}