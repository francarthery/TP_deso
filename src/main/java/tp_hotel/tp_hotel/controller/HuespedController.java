package tp_hotel.tp_hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tp_hotel.tp_hotel.exceptions.DocumentoExistenteException;
import tp_hotel.tp_hotel.exceptions.HuespedConEstadiaException;
import tp_hotel.tp_hotel.exceptions.HuespedNoEncontradoException;
import tp_hotel.tp_hotel.model.BusquedaHuespedDTO;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.HuespedDTO;
import tp_hotel.tp_hotel.service.GestorHuesped;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class HuespedController {

    private final GestorHuesped gestorHuesped;

    @Autowired
    public HuespedController(GestorHuesped gestorHuesped) {
        this.gestorHuesped = gestorHuesped;
    }

    @GetMapping
    public ResponseEntity<?> buscarHuesped(@Valid BusquedaHuespedDTO busquedaHuespedDTO) {
        
        try{
            List<Huesped> huespedes = gestorHuesped.buscarHuesped(busquedaHuespedDTO);
            List<HuespedDTO> huespedesDTO = huespedes.stream()
                .map(HuespedDTO::new)
                .toList();
            return ResponseEntity.status(HttpStatus.OK).body(huespedesDTO);
        } catch (HuespedNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHuespedById(@PathVariable int id) {
        try{
            Huesped huesped = gestorHuesped.buscarHuespedPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(huesped);
        }catch(HuespedNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/lista")
    public ResponseEntity<?> getHuespedesById(@RequestBody List<Integer> ids){
        try{
            List<Huesped> huespedes = gestorHuesped.buscarHuespedesPorId(ids);

            List<HuespedDTO> huespedesDTO = huespedes.stream()
            .map(HuespedDTO::new)
            .toList();
            
            return ResponseEntity.status(HttpStatus.OK).body(huespedesDTO);
        } catch(HuespedNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    
    @PostMapping
    public ResponseEntity<?> crearHuesped(@Valid @RequestBody HuespedDTO huespedDTO) {
        try {
            Huesped huesped = huespedDTO.toEntity();
            Huesped nuevoHuesped = gestorHuesped.registrarHuesped(huesped);
            return ResponseEntity.status(HttpStatus.CREATED).body(new HuespedDTO(nuevoHuesped));
        } catch (DocumentoExistenteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarHuesped(@PathVariable Integer id,
        @Valid @RequestBody HuespedDTO huespedDTO) {

        try {
            Huesped huespedAActualizar = huespedDTO.toEntity();
            gestorHuesped.modificarHuesped(huespedAActualizar);
            return ResponseEntity.ok(new HuespedDTO(huespedAActualizar));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DocumentoExistenteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
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
