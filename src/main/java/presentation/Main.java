package presentation;
import java.util.Scanner;

import service.GestorUsuario;

public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido al sistema de gestión hotelera.");
        
        //CU1 - Login
        GestorUsuario gestorUsuario = GestorUsuario.getInstancia();

        Scanner scanner = new Scanner(System.in);
        Login login = new Login(gestorUsuario);

        login.presentacionLogin(scanner);        

        //Menú Principal
        MenuPrincipal menuPrincipal = new MenuPrincipal();
        menuPrincipal.menuPrincipal(scanner);
        
        scanner.close();
    }
}
