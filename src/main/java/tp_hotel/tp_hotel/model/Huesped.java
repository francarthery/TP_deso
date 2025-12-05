package tp_hotel.tp_hotel.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @Column(nullable = false)
    private TipoDocumento tipoDocumento;
    
    @Column(length = 10, nullable = false)
    private String numeroDocumento;

    @Column(length = 20)
    private String cuit;
    
    @Enumerated(EnumType.STRING)
    private IVA posicionFrenteAlIVA;
    
    @Column(nullable = false)
    private LocalDate fechaDeNacimiento;

    @Column(length = 20, nullable = false)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 50, nullable = false)
    private String ocupacion;

    @Column(length = 85, nullable = false)
    private String nacionalidad;
    
    @Embedded
    private Direccion direccion;
    
    @OneToMany(mappedBy = "huespedTitular", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private List<Estadia> estadiasComoTitular = new ArrayList<>();

    @ManyToMany(mappedBy = "huespedInvitados")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private List<Estadia> estadiasComoInvitado = new ArrayList<>();

    @OneToOne(mappedBy = "huesped", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private PersonaFisica personaFisica;
    
    public void agregarEstadiaComoTitular(Estadia estadia) {
        this.estadiasComoTitular.add(estadia);
    }

    public void agregarEstadiaComoInvitado(Estadia estadia){
        this.estadiasComoInvitado.add(estadia);
    }
    
    public boolean esMayorDeEdad(){
        LocalDate hoy = LocalDate.now();
        Period edad = Period.between(this.fechaDeNacimiento, hoy);
        return edad.getYears() >= 18;
    }
}
