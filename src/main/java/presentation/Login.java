package presentation;
import java.util.Scanner;
import exceptions.UsuarioNoValidoException;
import service.GestorUsuario;

public class Login {
    
    private final GestorUsuario gestorUsuario;
    Login(GestorUsuario gestorUsuario) {
        this.gestorUsuario = gestorUsuario;
    }
    
    public boolean autenticar(Scanner scanner) {
        System.out.print("Ingrese su nombre de usuario: ");
        String username = scanner.nextLine().trim();
        System.out.print("Ingrese su contraseña: ");
        String password = scanner.nextLine().trim();
        
        try {
            return gestorUsuario.validarLogin(username, password);
            
        } catch (UsuarioNoValidoException e) {
            // System.out.println(e.getMessage());
            return false;
        }
    }

    public void presentacionLogin(Scanner scanner) {

        boolean loginIncorrecto = true;
        while(loginIncorrecto){
            if (autenticar(scanner)) {
                System.out.println("Login exitoso. Bienvenido al sistema de gestión hotelera.");
                loginIncorrecto = false;
            } else {
                System.out.println("Login fallido. Por favor, intente de nuevo.");
            }
        }
    }

}