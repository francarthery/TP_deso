package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fechaInicio;
    @Column(nullable = false)
    private LocalDate fechaFin;
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(length = 50, nullable = false)
    private String nombreHuesped;
    @Column(length = 50, nullable = false)
    private String apellidoHuesped;
    @Column(length = 20, nullable = false)
    private String telefonoHuesped;
    
    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "habitacion_numero", nullable = false)
    private Habitacion habitacion;
}