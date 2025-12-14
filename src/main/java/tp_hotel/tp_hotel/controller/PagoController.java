package tp_hotel.tp_hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tp_hotel.tp_hotel.service.GestorPago;

@RestController
@RequestMapping("/api/pago")
@CrossOrigin(origins = "http://localhost:3000")
public class PagoController {
    private GestorPago gestorPago;
    
    @Autowired
    public PagoController(GestorPago gestorPago){
        this.gestorPago = gestorPago;
    }

    
}
