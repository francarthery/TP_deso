package tp_hotel.tp_hotel.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.Rol; 
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

    public void validarLogin(String nombreUsuario, String contrasena) throws UsuarioNoValidoException {
        
        Optional<Usuario> usuario = usuarioRepository.findByUsername(nombreUsuario);

        if (usuario.isEmpty() || !usuario.get().getPassword().equals(contrasena)) {
            throw new UsuarioNoValidoException("Usuario o contraseña incorrectos.");
        }

    }

    public void logout() {
    }

    public void crearUsuario(Usuario u) {
    }

    public void modificarUsuario(Usuario u) {
    }

    public void eliminarUsuario(int id) {
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        return null;
    }

    public void asignarRol(Usuario u, Rol rol) {
    }
}