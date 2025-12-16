package tp_hotel.tp_hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.NotaCreditoNoExistenteException;
import tp_hotel.tp_hotel.model.NotaCredito;
import tp_hotel.tp_hotel.service.GestorNotaCredito;
import tp_hotel.tp_hotel.model.NotaCredito;
import tp_hotel.tp_hotel.repository.NotaCreditoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/notaCredito")
@CrossOrigin(origins = "http://localhost:3000")

public class NotaCreditoController {
    private final GestorNotaCredito gestorNotaCredito;

    @Autowired
    public NotaCreditoController(GestorNotaCredito gestorNotaCredito){
        this.gestorNotaCredito = gestorNotaCredito;
    }

    @PostMapping
    public ResponseEntity<?> crearNotaCredito(@RequestBody List<Integer> facturasIds) {
        try {
            NotaCredito nuevaNotaCredito = gestorNotaCredito.crearNotaCredito(facturasIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaNotaCredito.getId());
        } catch(FacturasNoExistentesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> generarPDFNotaCredito (@PathVariable Integer id) {
        try{
            byte[] pdfNotaCredito = gestorNotaCredito.generarPDFNotaCredito(id);
            return ResponseEntity.status(HttpStatus.OK).body(pdfNotaCredito);
        }catch(NotaCreditoNoExistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
}
