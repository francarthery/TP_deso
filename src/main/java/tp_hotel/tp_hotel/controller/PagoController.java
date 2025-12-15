package tp_hotel.tp_hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.PagoInsuficienteException;
import tp_hotel.tp_hotel.exceptions.TipoPagoIncorrectoException;
import tp_hotel.tp_hotel.model.Pago;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.model.TipoPagoDTO;
import tp_hotel.tp_hotel.service.GestorPago;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/pago")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class PagoController {
    private GestorPago gestorPago;
    
    @Autowired
    public PagoController(GestorPago gestorPago){
        this.gestorPago = gestorPago;
    }

    @PostMapping("/pagar/{facturaId}")
    public ResponseEntity<?> ingresarPago(@Valid @RequestBody List<TipoPagoDTO> tipoPago, @PathVariable Integer facturaId) {
        try{
            Integer pago = gestorPago.ingresarPago(tipoPago, facturaId);
            return ResponseEntity.status(HttpStatus.CREATED).body("El pago fue creado correctamente");
        } catch (FacturasNoExistentesException | TipoPagoIncorrectoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(PagoInsuficienteException e){
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }
}
