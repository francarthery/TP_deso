package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(length = 50, unique = true, nullable = false)
    private String numero; 

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private TipoFactura tipo;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    private Float total;

    @ManyToOne
    @JoinColumn(name = "responsable_id", nullable = false)
    private ResponsablePago responsableDePago;

    @OneToOne
    @JoinColumn(name = "estadia_id")
    private Estadia estadia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "factura_id")
    private List<DetalleFactura> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "factura")
    @ToString.Exclude
    private List<Pago> pagos = new ArrayList<>();
}