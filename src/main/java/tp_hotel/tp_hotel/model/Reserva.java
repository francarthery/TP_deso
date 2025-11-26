package tp_hotel.tp_hotel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "habitacion_numero", nullable = false)
    private Habitacion habitacion;

    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    @JsonIgnoreProperties({"reservas", "estadias"})
    private Huesped titular;

    @OneToOne(mappedBy = "reserva")
    @ToString.Exclude
    @JsonIgnore
    private Estadia estadia;
}