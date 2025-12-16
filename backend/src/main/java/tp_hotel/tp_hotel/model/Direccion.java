package tp_hotel.tp_hotel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {
    @Column(length = 100, nullable = false)
    private String calle;

    @Column(length = 10, nullable = false)
    private String numero;

    @Column(length = 5)
    private String departamento;

    @Column(length = 4)
    private String piso;

    @Column(length = 10, nullable = false)
    private String codigoPostal;

    @Column(length = 50, nullable = false)
    private String localidad;

    @Column(length = 50, nullable = false)
    private String provincia;

    @Column(length = 30, nullable = false)
    private String pais;
}