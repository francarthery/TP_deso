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
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(length = 12, nullable = false)
    private Float montoTotal;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pago_id")
    private List<TipoPago> formasDePago;

    public Float calcularTotalIngresos(){
        Float totalIngresos = 0.0f;

        if(this.formasDePago != null){
            for(TipoPago tp : formasDePago){
                totalIngresos += tp.getMonto();
            }
        }

        return totalIngresos;
    }
}