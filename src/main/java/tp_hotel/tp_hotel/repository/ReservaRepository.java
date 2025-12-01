package tp_hotel.tp_hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp_hotel.tp_hotel.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.numero = :numeroHabitacion AND r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<Reserva> findReservasSolapadas(@Param("numeroHabitacion") String numeroHabitacion, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<Reserva> findReservasPorFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param ("fechaFin") LocalDate fechaFin);
}