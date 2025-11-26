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
    public ResponseEntity<List<Huesped>> buscarHuesped(
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) TipoDocumento tipoDocumento,
            @RequestParam(required = false) String numeroDocumento) {
        List<Huesped> huespedes = gestorHuesped.buscarHuesped(apellido, nombres, tipoDocumento, numeroDocumento);
        return ResponseEntity.ok(huespedes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Huesped> getHuespedById(@PathVariable int id) {
        Huesped huesped = gestorHuesped.buscarHuespedPorId(id);
        if (huesped != null) {
            return ResponseEntity.ok(huesped);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearHuesped(@RequestBody Huesped huesped) {
        try {
            Huesped nuevoHuesped = gestorHuesped.registrarHuesped(huesped);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHuesped);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarHuesped(@PathVariable Integer id, @RequestBody Huesped huespedDetails) {
        Huesped existingHuesped = gestorHuesped.buscarHuespedPorId(id);
        if (existingHuesped == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields
        existingHuesped.setApellido(huespedDetails.getApellido());
        existingHuesped.setNombres(huespedDetails.getNombres());
        existingHuesped.setTipoDocumento(huespedDetails.getTipoDocumento());
        existingHuesped.setNumeroDocumento(huespedDetails.getNumeroDocumento());
        existingHuesped.setCuit(huespedDetails.getCuit());
        existingHuesped.setPosicionFrenteAlIVA(huespedDetails.getPosicionFrenteAlIVA());
        existingHuesped.setFechaDeNacimiento(huespedDetails.getFechaDeNacimiento());
        existingHuesped.setTelefono(huespedDetails.getTelefono());
        existingHuesped.setEmail(huespedDetails.getEmail());
        existingHuesped.setOcupacion(huespedDetails.getOcupacion());
        existingHuesped.setNacionalidad(huespedDetails.getNacionalidad());
        existingHuesped.setDireccion(huespedDetails.getDireccion());
        
        boolean success = gestorHuesped.modificarHuesped(existingHuesped);
        if (success) {
            return ResponseEntity.ok(existingHuesped);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el huésped.");
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
