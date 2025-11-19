package tp_hotel.tp_hotel.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Huesped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 50, nullable = false)
    private String apellido;

    @Column(length = 50, nullable = false)
    private String nombres;
    
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;
    
    @Column(length = 20, nullable = false, unique = true)
    private String numeroDocumento;

    @Column(length = 20)
    private String cuit;
    
    @Enumerated(EnumType.STRING)
    private IVA posicionFrenteAlIVA;
    
    private LocalDate fechaDeNacimiento;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 50)
    private String ocupacion;

    @Column(length = 50)
    private String nacionalidad;
    
    @Embedded
    private Direccion direccion;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Estadia> estadias = new ArrayList<>();

    public void agregarEstadia(Estadia estadia) {
        this.estadias.add(estadia);
    }
}
