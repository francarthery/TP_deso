package service;
import repository.UsuarioDAO;

public class GestorUsuario {

    private static GestorUsuario instancia;
    private UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();
            
    private GestorUsuario() {
    }
    
    public static GestorUsuario getInstancia() {
        if (instancia == null) {
            instancia = new GestorUsuario();
        }
        return instancia;
    }





















}
