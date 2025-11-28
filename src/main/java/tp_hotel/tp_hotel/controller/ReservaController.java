package tp_hotel.tp_hotel.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.model.ReservaDTO;
import tp_hotel.tp_hotel.service.GestorReserva;
import tp_hotel.tp_hotel.model.EstadiaDTO;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservaController {

    private final GestorReserva gestorReserva;

    @Autowired
    public ReservaController(GestorReserva gestorReserva) {
        this.gestorReserva = gestorReserva;
    }

    @PostMapping
    public ResponseEntity<?> crearReservas(@RequestBody List<ReservaDTO> reservasDTO) {
        try {
            List<Reserva> reservasCreadas = gestorReserva.crearReservas(reservasDTO);
            
            List<ReservaDTO> respuesta = reservasCreadas.stream()
                .map(ReservaDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar las reservas.");
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<?> verificarReservas(@RequestBody EstadiaDTO estadiaDTO){
        try {
            List<Reserva> reservasSolapadas = gestorReserva.buscarReservasSolapadas(
                estadiaDTO.getNumeroHabitacion(),
                estadiaDTO.getCheckIn(),
                estadiaDTO.getCheckOut()
            );
            
            List<ReservaDTO> respuesta = reservasSolapadas.stream()
                .map(ReservaDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al verificar reservas.");
        }
    }

}
