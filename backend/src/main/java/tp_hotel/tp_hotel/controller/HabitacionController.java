package tp_hotel.tp_hotel.controller;
import tp_hotel.tp_hotel.service.GestorHabitacion;
import tp_hotel.tp_hotel.service.GestorEstadia;
import tp_hotel.tp_hotel.model.EstadiaDTO;
import tp_hotel.tp_hotel.model.HabitacionEstadoDTO;
import tp_hotel.tp_hotel.exceptions.FechaDesdePosteriorHastaException;
import tp_hotel.tp_hotel.model.Estadia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class HabitacionController{
    
    private final GestorHabitacion gestorHabitacion;
    private final GestorEstadia gestorEstadia;

    @Autowired
    public HabitacionController(GestorHabitacion gestorHabitacion, GestorEstadia gestorEstadia){
        this.gestorHabitacion = gestorHabitacion;
        this.gestorEstadia = gestorEstadia;
    }

    @GetMapping("/estado")
    public ResponseEntity<?> mostrarEstadoHabitaciones(
        @RequestParam (required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam (required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta){
        
        try{
            List<HabitacionEstadoDTO> estadoHabitaciones = gestorHabitacion.obtenerEstadoHabitaciones(desde, hasta);
            return ResponseEntity.status(HttpStatus.OK).body(estadoHabitaciones);
        }catch(FechaDesdePosteriorHastaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/ocupar-habitacion")
    public ResponseEntity<?> ocuparHabitaciones(@Valid @RequestBody List<EstadiaDTO> estadiasDTO){
        try {
            List<Estadia> estadiasCreadas = gestorEstadia.crearEstadias(estadiasDTO);
            
            List<EstadiaDTO> respuesta = estadiasCreadas.stream()
                .map(EstadiaDTO::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cargar las estadias.");
        }
    }

}