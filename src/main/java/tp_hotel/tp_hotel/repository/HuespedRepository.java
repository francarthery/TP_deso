package tp_hotel.tp_hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.TipoDocumento;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
}