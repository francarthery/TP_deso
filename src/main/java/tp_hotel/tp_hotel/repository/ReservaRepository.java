package tp_hotel.tp_hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.model.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.numero = :numeroHabitacion AND r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio AND r.estado != 'CANCELADA'")
    List<Reserva> findReservasSolapadas(@Param("numeroHabitacion") String numeroHabitacion, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio AND r.estado != 'CANCELADA'")
    List<Reserva> findReservasPorFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM Reserva r WHERE (:nombreHuesped IS NULL OR r.nombreHuesped LIKE CONCAT(:nombreHuesped, '%'))" + 
            "AND r.apellidoHuesped LIKE CONCAT(:apellidoHuesped, '%') and r.estado != :estado")
    List<Reserva> findByApellidoHuespedNombreHuesped(@Param("apellidoHuesped") String apellidoHuesped, 
                                                      @Param("nombreHuesped") String nombreHuesped, 
                                                      @Param("estado") EstadoReserva estado);

    @Modifying
    @Query("UPDATE Reserva r SET r.estado = :estado WHERE r.id IN :ids")
    void cancelarReservas(@Param("ids") List<Integer> ids, @Param("estado") EstadoReserva estado);
}