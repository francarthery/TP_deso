package tp_hotel.tp_hotel.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Consumo;
import tp_hotel.tp_hotel.model.Estadia;

@Repository
public interface ConsumoRepository extends JpaRepository<Consumo, Integer> {
    List<Consumo> findByEstadia(Estadia estadia);
}