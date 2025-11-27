package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;
}