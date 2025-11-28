package tp_hotel.tp_hotel.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.TipoPago;

@Repository
public interface TipoPagoRepository extends JpaRepository<TipoPago, Integer> {

    // @Query("SELECT tp FROM Pago pago JOIN pago.formasDePago tp WHERE pago.factura.numero = :numero")
    // List<TipoPago> findByFacturaNumero(@Param("numero") String numero);
}