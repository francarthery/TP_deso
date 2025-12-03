package tp_hotel.tp_hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Estadia;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Long> {
    List<Estadia> findByHabitacionNumero(String numero);

    @Query("SELECT e FROM Estadia e WHERE e.checkIn <= :fechaFin AND e.checkOut >= :fechaInicio")
    List<Estadia> findEstadiasPorFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param ("fechaFin") LocalDate fechaFin);
}
