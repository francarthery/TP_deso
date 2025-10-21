package TpDeso.service;
import TpDeso.domain.UsuarioDTO;
import TpDeso.exceptions.UsuarioNoValidoException;
import TpDeso.repository.UsuarioDAO;

public class GestorUsuario {

    private static GestorUsuario instancia;
    private UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();
            
    private GestorUsuario() { //Constructor privado para singleton
    }
    
    public static GestorUsuario getInstancia() {
        if (instancia == null) {
            instancia = new GestorUsuario();
        }
        return instancia;
    }

    public boolean validarLogin(String nombreUsuario, String contrasena) throws UsuarioNoValidoException {
        
        UsuarioDTO usuario = usuarioDAO.encontrarPorUsername(nombreUsuario);

        if (usuario == null || !usuario.getPassword().equals(contrasena)) {
            throw new UsuarioNoValidoException("Usuario o contraseña incorrectos.");
        }

        return true;
    }
    





















}
