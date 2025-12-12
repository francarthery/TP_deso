package tp_hotel.tp_hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import tp_hotel.tp_hotel.exceptions.PersonaJuridicaNoExistenteException;
import tp_hotel.tp_hotel.model.BusquedaResponsablePagoDTO;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.model.ResponsablePago;
import tp_hotel.tp_hotel.repository.HuespedRepository;
import tp_hotel.tp_hotel.repository.PersonaFisicaRepository;
import tp_hotel.tp_hotel.repository.PersonaJuridicaRepository;

@Service
public class GestorResponsablePago {

    private final HuespedRepository huespedRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final PersonaFisicaRepository personaFisicaRepository;

    @Autowired
    public GestorResponsablePago(PersonaJuridicaRepository personaJuridicaRepository, PersonaFisicaRepository personaFisicaRepository, HuespedRepository huespedRepository) {
        this.personaJuridicaRepository = personaJuridicaRepository;
        this.personaFisicaRepository = personaFisicaRepository;
        this.huespedRepository = huespedRepository;
    }

    public List<PersonaJuridica> buscarPersonaJuridica(BusquedaResponsablePagoDTO busquedaResponsablePagoDTO) {
        List<PersonaJuridica> responsables = personaJuridicaRepository.findByCuitYRazonSocial(busquedaResponsablePagoDTO.getCuit(), busquedaResponsablePagoDTO.getRazonSocial());
        
        if(responsables.isEmpty()){
            throw new PersonaJuridicaNoExistenteException("No se encontró un responsable pago para los atributos proporcionados.");
        }

        return responsables;
        
    }
    
    public ResponsablePago darAltaPersonaFisica(Integer idPersonaFisica) {
        Optional<PersonaFisica> personaFisica = personaFisicaRepository.findByHuespedId(idPersonaFisica);

        if(personaFisica.isPresent()) return personaFisica.get();

        Optional<Huesped> huesped = huespedRepository.findById(idPersonaFisica);
        if(!huesped.isPresent()) {
            throw new IllegalArgumentException("No se encontró un huésped con el identificador proporcionado.");
        }
        PersonaFisica personaFisicaNueva = new PersonaFisica();
        personaFisicaNueva.setHuesped(huesped.get());
        personaFisicaRepository.save(personaFisicaNueva);
        
        return personaFisicaNueva;
    }

    public void modificarResponsable(PersonaJuridica r) {
    }

    public void darBajaResponsable(PersonaJuridica r) {
    }

    public ResponsablePago darAltaPersonaJuridica(PersonaJuridica personaJuridica) {
       return personaJuridicaRepository.save(personaJuridica);
    }

}