package tp_hotel.tp_hotel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp_hotel.tp_hotel.model.CategoriaHabitacion;
import tp_hotel.tp_hotel.model.Habitacion;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
    Optional<Habitacion> findByNumero(String numero);

    @Query("SELECT habitacion FROM Habitacion WHERE habitacion.categoria = :categoria AND habitacion.id NOT IN " +
           "(SELECT reserva.habitacion.id FROM Reserva WHERE " +
           "reserva.estado <> 'CANCELADA' AND " +
           "(:desde <= reserva.fechaFin AND :hasta >= reserva.fechaInicio))")
    List<Habitacion> buscarDisponibles(@Param("categoria") CategoriaHabitacion categoria,
                                       @Param("desde") LocalDate desde,
                                       @Param("hasta") LocalDate hasta);
}