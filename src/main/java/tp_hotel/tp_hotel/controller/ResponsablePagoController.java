package tp_hotel.tp_hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tp_hotel.tp_hotel.exceptions.CuitYaExistenteException;
import tp_hotel.tp_hotel.exceptions.PersonaJuridicaNoExistenteException;
import tp_hotel.tp_hotel.exceptions.ResponsablePagoNoExistenteException;
import tp_hotel.tp_hotel.model.BusquedaResponsablePagoDTO;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.model.PersonaJuridicaDTO;
import tp_hotel.tp_hotel.model.ResponsablePago;
import tp_hotel.tp_hotel.service.GestorResponsablePago;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/responsablePago")
@CrossOrigin(origins = "http://localhost:3000")

@Validated
public class ResponsablePagoController {
    private final GestorResponsablePago gestorResponsablePago;

    @Autowired
    public ResponsablePagoController(GestorResponsablePago gestorResponsablePago){
        this.gestorResponsablePago = gestorResponsablePago;
    }

    @PostMapping("/personaFisica/{idHuesped}")
    public ResponseEntity<?> darAltaPersonaFisica(@PathVariable Integer idHuesped){
        try{
            ResponsablePago personaFisica = gestorResponsablePago.darAltaPersonaFisica(idHuesped);
            return ResponseEntity.status(HttpStatus.CREATED).body(personaFisica.getId()); 
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); 
        }
    }

    @PostMapping("/personaJuridica")
    public ResponseEntity<?> darAltaPersonaJuridica(@Valid @RequestBody PersonaJuridicaDTO personaJuridicaDTO){
        try{
            PersonaJuridica personaJuridica = personaJuridicaDTO.toEntity();
            ResponsablePago respuesta = gestorResponsablePago.darAltaPersonaJuridica(personaJuridica);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta.getId());
        }catch(CuitYaExistenteException c){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(c.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); 
        }
    }
    
    @GetMapping
    public ResponseEntity<?> buscarPersonaJuridica(@Valid BusquedaResponsablePagoDTO busquedaResponsableDTO) {
        try{
            List<PersonaJuridica> responsablesPago = gestorResponsablePago.buscarPersonaJuridica(busquedaResponsableDTO);
            List<PersonaJuridicaDTO> respuesta = responsablesPago.stream()
                .map(PersonaJuridicaDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(respuesta);
        }catch(PersonaJuridicaNoExistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> darBajaResponsablePago(@PathVariable Integer id){
    //     try{
    //         gestorResponsablePago.darBajaResponsable(id);
    //         return ResponseEntity.status(HttpStatus.OK).body("El responsable de pago ha sido eliminado exitosamente.");
    //     }catch(ResponsablePagoNoExistenteException e){
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    //     }
    // }
    
}
