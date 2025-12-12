package tp_hotel.tp_hotel.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.model.ResponsablePago;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    Optional<PersonaJuridica> findByCuit(String cuit);
    void deleteByCuit(String cuit);
    
    @Query("SELECT p FROM PersonaJuridica p WHERE (:cuit IS NULL OR p.cuit = :cuit) AND (:razonSocial IS NULL OR p.razonSocial LIKE CONCAT(:razonSocial, '%'))")
    List<PersonaJuridica> findByCuitYRazonSocial(@Param("cuit") String cuit, @Param("razonSocial") String razonSocial);
}