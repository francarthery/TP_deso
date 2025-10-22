package tpdeso.presentation;
import tpdeso.presentation.Login;
import tpdeso.service.GestorUsuario;


public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido al sistema de gestión hotelera.");
        
        GestorUsuario gestorUsuario = GestorUsuario.getInstancia();
        Login login = new Login(gestorUsuario);

        login.presentacionLogin();        
    }
}
