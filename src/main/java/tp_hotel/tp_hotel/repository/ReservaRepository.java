package tp_hotel.tp_hotel.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
}