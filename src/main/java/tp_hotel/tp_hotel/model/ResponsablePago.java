package tp_hotel.tp_hotel.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ResponsablePago {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
}