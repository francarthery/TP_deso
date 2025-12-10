package tp_hotel.tp_hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import tp_hotel.tp_hotel.service.GestorEstadia;
import tp_hotel.tp_hotel.model.EstadiaDTO;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.exceptions.HabitacionNoExistenteException;

@RestController
@RequestMapping("/api/estadias")
@CrossOrigin(origins = "http://localhost:3000")

public class EstadiaController {
    private final GestorEstadia gestorEstadia;
    
    @Autowired
    public EstadiaController(GestorEstadia gestorEstadia){
        this.gestorEstadia = gestorEstadia;
    }
    
    @GetMapping("/{numeroHabitacion}")
    public ResponseEntity<?> buscarEstadia(@PathVariable String numeroHabitacion){
        try{
            Estadia estadia = gestorEstadia.buscarEstadiaActiva(numeroHabitacion);
            return ResponseEntity.status(HttpStatus.OK).body(new EstadiaDTO(estadia));
        } 
        catch(HabitacionNoExistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }   
    }

            

}
