package tp_hotel.tp_hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.TipoDocumento;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
    
    List<Huesped> findByApellido(String apellido);

    void deleteByNumeroDocumento(String numeroDocumento);

    Optional<Huesped> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

    @Query("SELECT h FROM Huesped h WHERE " +
           "(:apellido IS NULL OR h.apellido LIKE CONCAT(:apellido, '%')) AND " +
           "(:nombres IS NULL OR h.nombres LIKE CONCAT(:nombres, '%')) AND " +
           "(:tipoDocumento IS NULL OR h.tipoDocumento = :tipoDocumento) AND " +
           "(:numeroDocumento IS NULL OR h.numeroDocumento = :numeroDocumento)")
    List<Huesped> buscarConFiltros(
        @Param("apellido") String apellido, 
        @Param("nombres") String nombres, 
        @Param("tipoDocumento") TipoDocumento tipoDocumento, 
        @Param("numeroDocumento") String numeroDocumento
    );
}