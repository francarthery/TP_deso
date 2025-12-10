package tp_hotel.tp_hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tp_hotel.tp_hotel.service.GestorResponsablePago;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/responsablePago")
@CrossOrigin(origins = "http://localhost:3000")

public class ResponsablePagoController {
    private final GestorResponsablePago gestorResponsablePago;

    @Autowired
    public ResponsablePagoController(GestorResponsablePago gestorResponsablePago){
        this.gestorResponsablePago = gestorResponsablePago;
    }

    // @PostMapping()
    // public String darAltaResponsablePago() {
    //     pass;
    // }

    // @DeleteMapping()
    // public boolean darBajaResponsablePago(){
        
    // }

    // @GetMapping()
    // public List<ResponsablePago> buscarResponsablePago() {
        
    // }
    
    
}
