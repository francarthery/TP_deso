package presentation;

import repository.EstadiaDAO;
import repository.HuespedDAO;
import repository.UsuarioDAO;
import service.GestorEstadia;
import service.GestorHuesped;
import service.GestorUsuario;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido al sistema de gestión hotelera.");
        
        GestorUsuario gestorUsuario = new GestorUsuario(UsuarioDAO.getInstancia());
        GestorHuesped gestorHuesped = new GestorHuesped(HuespedDAO.getInstancia());
        GestorEstadia gestorEstadia = new GestorEstadia(EstadiaDAO.getInstancia());

        Scanner scanner = new Scanner(System.in);
        Login login = new Login(gestorUsuario);

        login.presentacionLogin(scanner);        

        //Menú Principal
        MenuPrincipal menuPrincipal = new MenuPrincipal(gestorHuesped, gestorEstadia);
        menuPrincipal.menuPrincipal(scanner);
        
        scanner.close();
    }
}
