package tp_hotel.tp_hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

}