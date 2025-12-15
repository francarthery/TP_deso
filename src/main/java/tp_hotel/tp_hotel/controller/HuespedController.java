package tp_hotel.tp_hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tp_hotel.tp_hotel.exceptions.HuespedConEstadiaException;
import tp_hotel.tp_hotel.exceptions.HuespedNoEncontradoException;
import tp_hotel.tp_hotel.model.BusquedaHuespedDTO;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.HuespedDTO;
import tp_hotel.tp_hotel.service.GestorEstadia;
import tp_hotel.tp_hotel.service.GestorHuesped;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class HuespedController {

    private final GestorHuesped gestorHuesped;
    private final GestorEstadia gestorEstadia;

    @Autowired
    public HuespedController(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia) {
        this.gestorHuesped = gestorHuesped;
        this.gestorEstadia = gestorEstadia;
    }

    @GetMapping
    public ResponseEntity<List<HuespedDTO>> buscarHuesped(@Valid BusquedaHuespedDTO busquedaHuespedDTO) {
        
        try{
            List<Huesped> huespedes = gestorHuesped.buscarHuesped(busquedaHuespedDTO);
            List<HuespedDTO> huespedesDTO = huespedes.stream()
                .map(HuespedDTO::new)
                .toList();

            return ResponseEntity.ok(huespedesDTO);
        } catch (HuespedNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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

    @PostMapping("/lista")
    public ResponseEntity<?> getHuespedesById(@RequestBody List<Integer> ids){
        try{
            List<Huesped> huespedes = gestorHuesped.buscarHuespedesPorId(ids);

            List<HuespedDTO> huespedesDTO = huespedes.stream()
            .map(HuespedDTO::new)
            .toList();
            
            return ResponseEntity.ok(huespedesDTO);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    
    @PostMapping
    public ResponseEntity<?> crearHuesped(
        @Valid @RequestBody HuespedDTO huespedDTO,
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
        @Valid @RequestBody HuespedDTO huespedDetailsDTO,
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
        } catch (HuespedNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaHuesped(@PathVariable Integer id) {
        try{
            gestorHuesped.darBajaHuesped(id);
            return ResponseEntity.status(HttpStatus.OK).body("Huésped eliminado exitosamente.");
        }catch(HuespedNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); 
        }catch(HuespedConEstadiaException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
