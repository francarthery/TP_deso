package tp_hotel.tp_hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.TipoDocumento;
import tp_hotel.tp_hotel.model.TipoFactura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    @Query("SELECT MAX(f.numero) FROM Factura f WHERE f.tipo = :tipo")
    String findUltimoNumeroPorTipo(TipoFactura tipo);
    Optional<Factura> findByNumero(String numero);

    List<Factura> findByEstado(EstadoFactura estado);

    List<Factura> findByEstadia(Estadia estadia);

    @Query("SELECT f FROM Factura f WHERE f.estadia.habitacion.numero = :numeroHabitacion AND f.estado = :estado")
    List<Factura> findByNumeroHabitacionYEstado(@Param("numeroHabitacion") String numeroHabitacion, @Param("estado") EstadoFactura estado);
    
    void deleteByNumero(String numero);

    @Query("SELECT f FROM Factura f WHERE f.responsablePago.id = :id")
    List<Factura> findByResponsableId(@Param("id") Integer id);

    @Query("SELECT f FROM Factura f " +
           "LEFT JOIN PersonaFisica pf ON f.responsablePago.id = pf.id " +
           "LEFT JOIN pf.huesped h " +
           "LEFT JOIN PersonaJuridica pj ON f.responsablePago.id = pj.id " +
           "WHERE ((h.tipoDocumento = :tipoDocumento AND h.numeroDocumento = :numeroDocumento) OR h.cuit = :cuit OR pj.cuit = :cuit) AND f.estado = :estado")
    List<Factura> buscarFacturasPorResponsable( @Param("cuit") String cuit,
                                                @Param("tipoDocumento") TipoDocumento tipoDocumento,
                                                @Param("numeroDocumento") String numeroDocumento,
                                                @Param("estado") EstadoFactura estado);
}