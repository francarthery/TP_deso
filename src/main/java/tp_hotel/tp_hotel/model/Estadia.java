package tp_hotel.tp_hotel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estadia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @ManyToOne
    @JoinColumn(name = "huesped_titular_id", nullable = false)
    private Huesped huespedTitular;

    @ManyToMany
    @JoinTable(
        name = "estadia_huespedes_invitados",
        joinColumns = @JoinColumn(name = "estadia_id"),
        inverseJoinColumns = @JoinColumn(name = "huesped_id")
    )
    @ToString.Exclude
    private List<Huesped> huespedInvitados = new ArrayList<>();

    @OneToMany(mappedBy = "estadia", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Consumo> consumos = new ArrayList<>();
    
    @OneToOne(mappedBy = "estadia", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private Factura factura;
}
