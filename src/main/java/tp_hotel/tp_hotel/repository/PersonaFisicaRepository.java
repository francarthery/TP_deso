package tp_hotel.tp_hotel.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.PersonaJuridica;

@Repository
public interface PersonaFisicaRepository extends JpaRepository<PersonaFisica, Integer> {
    @Query("SELECT p FROM PersonaFisica p WHERE p.huesped.id = :id")
    Optional<PersonaFisica> findByHuespedId(@Param("id") Integer id);
    void deleteById(Integer id);

    @Query("SELECT p FROM PersonaFisica p WHERE (:cuit IS NULL OR p.huesped.cuit = :cuit) AND (:razonSocial IS NULL OR p.razonSocial LIKE CONCAT(:razonSocial, '%'))")
    List<PersonaFisica> findByCuitYRazonSocial(@Param("cuit") String cuit, @Param("razonSocial") String razonSocial);
}