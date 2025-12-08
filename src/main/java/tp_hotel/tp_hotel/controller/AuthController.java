package tp_hotel.tp_hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import tp_hotel.tp_hotel.exceptions.UsuarioNoValidoException;
import tp_hotel.tp_hotel.model.UsuarioDTO;
import tp_hotel.tp_hotel.service.GestorUsuario;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class AuthController {

    private final GestorUsuario gestorUsuario;

    @Autowired
    public AuthController(GestorUsuario gestorUsuario) {
        this.gestorUsuario = gestorUsuario;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioDTO loginRequest) {
        try {
            boolean isValid = gestorUsuario.validarLogin(loginRequest.getUsername(), loginRequest.getPassword());
            if (isValid) {
                return ResponseEntity.ok().body("Login exitoso");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
            }
        } catch (UsuarioNoValidoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
