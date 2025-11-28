package tp_hotel.tp_hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    // Optional<Factura> findByNumero(String numero);

    List<Factura> findByEstado(EstadoFactura estado);

    List<Factura> findByEstadia(Estadia estadia);

    // void deleteByNumero(String numero);
}