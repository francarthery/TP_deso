package tp_hotel.tp_hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.Usuario;
import tp_hotel.tp_hotel.exceptions.UsuarioNoValidoException;
import tp_hotel.tp_hotel.repository.UsuarioRepository;

@Service
public class GestorUsuario {

    private final UsuarioRepository usuarioRepository;
            
    @Autowired
    public GestorUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean validarLogin(String nombreUsuario, String contrasena) throws UsuarioNoValidoException {
        
        Usuario usuario = usuarioRepository.findByUsername(nombreUsuario).orElse(null);

        if (usuario == null || !usuario.getPassword().equals(contrasena)) {
            throw new UsuarioNoValidoException("Usuario o contraseña incorrectos.");
        }

        return true;
    }
}
