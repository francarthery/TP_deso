package tp_hotel.tp_hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tp_hotel.tp_hotel.service.GestorFacturacion;
import tp_hotel.tp_hotel.exceptions.EstadiaNoExistenteException;
import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.ResponsablePagoNoExistenteException;
import tp_hotel.tp_hotel.model.BusquedaFacturaResponsableDTO;
import tp_hotel.tp_hotel.model.DatosFacturaDTO;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.FacturaDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/facturar")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class FacturaController {
    private final GestorFacturacion gestorFacturacion;
    
    @Autowired
    public FacturaController (GestorFacturacion gestorFacturacion) {
        this.gestorFacturacion = gestorFacturacion;
    }

    @PostMapping()
    public ResponseEntity<?> generarFactura(@RequestBody DatosFacturaDTO datosFacturaDTO){
        try{
            Integer idFactura = gestorFacturacion.generarFactura(datosFacturaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(idFactura);
        } catch(EstadiaNoExistenteException | ResponsablePagoNoExistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("pdf/{id}")
    public ResponseEntity<?> generarPDFFactura(@PathVariable Integer id) {
        try{
            byte[] pdfFactura = gestorFacturacion.generarPDFFactura(id);
            return ResponseEntity.status(HttpStatus.OK).body(pdfFactura);
        }catch(FacturasNoExistentesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    
    @GetMapping("/{numeroHabitacion}")
    public ResponseEntity<?> obtenerFacturas(@PathVariable String numeroHabitacion) {
        try{
            List<Factura> facturas = gestorFacturacion.obtenerFacturasPorHabitacionNoPagas(numeroHabitacion);
            List<FacturaDTO> facturaDTO = facturas.stream()
                .map(FacturaDTO::new)
                .toList();
            return ResponseEntity.ok(facturaDTO);
        }catch(FacturasNoExistentesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> obtenerFacturasPorResponsable(@Valid BusquedaFacturaResponsableDTO busquedaFacturaResponsable){
        try{
            List<Factura> facturas = gestorFacturacion.obtenerFacturasNoPagasPorResponsable(busquedaFacturaResponsable);
            List<FacturaDTO> facturaDTO = facturas.stream()
                .map(FacturaDTO::new)
                .toList();
            return ResponseEntity.status(HttpStatus.FOUND).body(facturaDTO);
        } catch(FacturasNoExistentesException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
