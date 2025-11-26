package tp_hotel.tp_hotel.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp_hotel.tp_hotel.model.PersonaJuridica;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    Optional<PersonaJuridica> findByCuit(String cuit);
    void deleteByCuit(String cuit);
}