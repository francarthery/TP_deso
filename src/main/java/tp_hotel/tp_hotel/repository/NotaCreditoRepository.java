package tp_hotel.tp_hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.NotaCredito;

@Repository
public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Integer> {
    @Query("SELECT MAX(n.numero) FROM NotaCredito n")
    String findUltimoNumero();
}