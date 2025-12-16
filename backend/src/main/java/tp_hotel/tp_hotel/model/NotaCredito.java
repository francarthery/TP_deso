package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, unique = true)
    private String numero;
    
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 12, nullable = false)
    private Float monto;

    @OneToMany
    @JoinColumn(name = "factura_id")
    private List<Factura> facturas;

    public void calcularMonto(){
        Float total = 0f;

        for(Factura f : facturas){
            total+= f.getTotal();
        }

        this.monto = total;
    }
}