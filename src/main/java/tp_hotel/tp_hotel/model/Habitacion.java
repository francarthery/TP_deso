package tp_hotel.tp_hotel.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {
    
    @Id
    @Column(length = 4, nullable = false)
    private String numero;

    @Column(nullable = false)
    private Float costoNoche;

    @Enumerated(EnumType.STRING)
    private CategoriaHabitacion categoria;

    @Enumerated(EnumType.STRING)
    private EstadoHabitacion estadoActual;

    @OneToMany(mappedBy = "habitacion")
    @ToString.Exclude
    @JsonIgnore
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "habitacion")
    @ToString.Exclude
    @JsonIgnore
    private List<Estadia> estadias = new ArrayList<>();
}
