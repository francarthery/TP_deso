package tp_hotel.tp_hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.HuespedDTO;
import tp_hotel.tp_hotel.model.TipoDocumento;
import tp_hotel.tp_hotel.service.GestorEstadia;
import tp_hotel.tp_hotel.service.GestorHuesped;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "http://localhost:3000")
public class HuespedController {

    private final GestorHuesped gestorHuesped;
    private final GestorEstadia gestorEstadia;

    @Autowired
    public HuespedController(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia) {
        this.gestorHuesped = gestorHuesped;
        this.gestorEstadia = gestorEstadia;
    }

    @GetMapping
    public ResponseEntity<List<HuespedDTO>> buscarHuesped(
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) TipoDocumento tipoDocumento,
            @RequestParam(required = false) String numeroDocumento) {
        List<Huesped> huespedes = gestorHuesped.buscarHuesped(apellido, nombres, tipoDocumento, numeroDocumento);
        List<HuespedDTO> huespedesDTO = huespedes.stream()
            .map(HuespedDTO::new)
            .toList();

        return ResponseEntity.ok(huespedesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HuespedDTO> getHuespedById(@PathVariable int id) {
        Huesped huesped = gestorHuesped.buscarHuespedPorId(id);
        if (huesped != null) {
            return ResponseEntity.ok(new HuespedDTO(huesped));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearHuesped(
        @RequestBody HuespedDTO huespedDTO,
        @RequestParam (required = false, defaultValue = "true") Boolean dniUnico) {
        try {
            Huesped huesped = huespedDTO.toEntity();
            Huesped nuevoHuesped = gestorHuesped.registrarHuesped(huesped, dniUnico);
            return ResponseEntity.status(HttpStatus.CREATED).body(new HuespedDTO(nuevoHuesped));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarHuesped(@PathVariable Integer id, 
        @RequestBody HuespedDTO huespedDetailsDTO,
        @RequestParam (required = false, defaultValue = "true") Boolean dniUnico) {
        if (gestorHuesped.buscarHuespedPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }

        Huesped huespedAActualizar = huespedDetailsDTO.toEntity();
        huespedAActualizar.setId(id);
        
        try {
            boolean success = gestorHuesped.modificarHuesped(huespedAActualizar, dniUnico);
            if (success) {
                return ResponseEntity.ok(new HuespedDTO(huespedAActualizar));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el huésped.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaHuesped(@PathVariable int id) {
        if (gestorEstadia.tieneEstadia(id)) {
             return ResponseEntity.status(HttpStatus.CONFLICT).body("El huésped no puede ser eliminado pues se ha alojado en el Hotel.");
        }
        
        Huesped huesped = gestorHuesped.buscarHuespedPorId(id);
        if (huesped == null) {
            return ResponseEntity.notFound().build();
        }

        boolean success = gestorHuesped.darBajaHuesped(huesped);
        if (success) {
            return ResponseEntity.ok("Huésped eliminado exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el huésped.");
        }
    }
}