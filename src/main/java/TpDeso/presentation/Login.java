package TpDeso.presentation;
import java.util.Scanner;

import TpDeso.exceptions.UsuarioNoValidoException;
import TpDeso.service.GestorUsuario;

public class Login {
    
    private final GestorUsuario gestorUsuario;
    Login(GestorUsuario gestorUsuario) {
        this.gestorUsuario = gestorUsuario;
    }
    
    public boolean autenticar() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su nombre de usuario: ");
        String username = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String password = scanner.nextLine();
        
        try {
            return gestorUsuario.validarLogin(username, password);
            
        } catch (UsuarioNoValidoException e) {
            // System.out.println(e.getMessage());
            return false;
        }
    }

    public void presentacionLogin() {

        if (autenticar()) {
            System.out.println("Login exitoso. Bienvenido al sistema de gestión hotelera.");
        } else {
            System.out.println("Login fallido. Por favor, intente de nuevo.");
        }

    }
    
    

    



}