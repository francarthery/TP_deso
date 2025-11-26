package tp_hotel.tp_hotel.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.repository.PersonaJuridicaRepository;

@Service
public class GestorResponsablePago {

    private final PersonaJuridicaRepository personaJuridicaRepository;

    @Autowired
    public GestorResponsablePago(PersonaJuridicaRepository personaJuridicaRepository) {
        this.personaJuridicaRepository = personaJuridicaRepository;
    }

    public List<PersonaJuridica> buscarResponsable(String razonSocial, String cuit) {
        return null;
    }

    public void darAltaResponsable(PersonaJuridica r) {
    }

    public void modificarResponsable(PersonaJuridica r) {
    }

    public void darBajaResponsable(PersonaJuridica r) {
    }
}