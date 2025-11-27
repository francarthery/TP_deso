package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
public class ResponsablePago {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "persona_fisica_id", unique = true)
  private PersonaFisica personaFisica;

  @OneToOne
  @JoinColumn(name = "persona_juridica_id", unique = true)
  private PersonaJuridica personaJuridica;
}