package tp_hotel.tp_hotel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Estadia;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Integer> {
    List<Estadia> findByHabitacionNumero(String numero);

    @Query("SELECT e FROM Estadia e WHERE e.checkIn <= :fechaFin AND e.checkOut >= :fechaInicio")
    List<Estadia> findEstadiasPorFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param ("fechaFin") LocalDate fechaFin);

    @Query("SELECT e FROM Estadia e WHERE e.habitacion.numero = :numeroHabitacion AND e.checkIn <= :now AND e.checkOut >= :now")
    Optional<Estadia> findActivaByHabitacionNumero(@Param("numeroHabitacion") String numeroHabitacion, @Param("now") LocalDate now);

    @Query("SELECT e FROM Estadia e WHERE e.habitacion.numero = :numeroHabitacion AND e.checkIn < :checkOut AND e.checkOut > :checkIn")
    Estadia findByNumeroYRango(@Param("numeroHabitacion") String numeroHabitacion, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);
}

