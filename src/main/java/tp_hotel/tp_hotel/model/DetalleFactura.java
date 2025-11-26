package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false)
    private Float precioUnitario;
    
    @Column(nullable = false)
    private Float subtotal;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;
}