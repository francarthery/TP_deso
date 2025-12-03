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
    
    @Column(length = 13, nullable = false)
    private String numero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private TipoFactura tipo;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    @Column(length = 12, nullable = false)
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

    @OneToOne(mappedBy = "factura", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Pago pago;

    public void agregarDetalleFactura(DetalleFactura detalleFactura){
        this.detalles.add(detalleFactura);
        this.total+= detalleFactura.getSubtotal();
    }

    public void registrarPago(Pago nuevoPago) {
        Float totalIngresado = nuevoPago.calcularTotalIngresos();

        nuevoPago.setMontoTotal(totalIngresado);

        if (totalIngresado >= this.total) {
            this.pago = nuevoPago;
            this.estado = EstadoFactura.PAGADA;
            nuevoPago.setFactura(this);
        } else {
            throw new RuntimeException("El monto de los medios de pago no cubre el total de la factura");
        }
    }
}