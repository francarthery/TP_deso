package service;
import domain.UsuarioDTO;
import exceptions.UsuarioNoValidoException;
import repository.UsuarioDAO;

public class GestorUsuario {

    private final UsuarioDAO usuarioDAO;
            
    public GestorUsuario(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public boolean validarLogin(String nombreUsuario, String contrasena) throws UsuarioNoValidoException {
        
        UsuarioDTO usuario = usuarioDAO.encontrarPorUsername(nombreUsuario);

        if (usuario == null || !usuario.getPassword().equals(contrasena)) {
            throw new UsuarioNoValidoException("Usuario o contraseña incorrectos.");
        }

        return true;
    }
    





















}
