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

import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.model.ReservaDTO;
import tp_hotel.tp_hotel.service.GestorReserva;

@RestController
@RequestMapping("/api/reservas")
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
            
            // Convertir a DTO para la respuesta (opcional, pero recomendado para no exponer entidades)
            List<ReservaDTO> respuesta = reservasCreadas.stream().map(r -> {
                ReservaDTO dto = new ReservaDTO();
                dto.setId(r.getId());
                dto.setFechaInicio(r.getFechaInicio());
                dto.setFechaFin(r.getFechaFin());
                dto.setEstado(r.getEstado());
                dto.setFechaCreacion(r.getFechaCreacion());
                dto.setIdHabitacion(r.getHabitacion().getId());
                dto.setNumeroHabitacion(r.getHabitacion().getNumero());
                dto.setIdHuesped(r.getTitular().getId());
                dto.setNombreHuesped(r.getTitular().getNombres());
                dto.setApellidoHuesped(r.getTitular().getApellido());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar las reservas.");
        }
    }
}
