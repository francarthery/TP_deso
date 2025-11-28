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
public interface HabitacionRepository extends JpaRepository<Habitacion, String> {

    @Query("SELECT h FROM Habitacion h WHERE h.categoria = :categoria AND h.numero NOT IN " +
           "(SELECT r.habitacion.numero FROM Reserva r WHERE " +
           "r.estado <> 'CANCELADA' AND " +
           "(:desde <= r.fechaFin AND :hasta >= r.fechaInicio))")
    List<Habitacion> buscarDisponibles(@Param("categoria") CategoriaHabitacion categoria,
                                       @Param("desde") LocalDate desde,
                                       @Param("hasta") LocalDate hasta);
}