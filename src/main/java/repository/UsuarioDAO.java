package repository;

public class UsuarioDAO {
    
    private static UsuarioDAO instance;

    private UsuarioDAO() {
        // Constructor privado para evitar instanciación externa
    }
    
    public static UsuarioDAO getInstancia() {
        if (instance == null) {
            instance = new UsuarioDAO();
        }
        return instance;
    }

}
