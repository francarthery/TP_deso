package ar.edu.utn.frsf.tpdeso.presentation;
import java.util.Scanner;

import ar.edu.utn.frsf.tpdeso.presentation.Login;
import ar.edu.utn.frsf.tpdeso.service.GestorUsuario;


public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido al sistema de gestión hotelera.");
        
        GestorUsuario gestorUsuario = GestorUsuario.getInstancia();
        Login login = new Login(gestorUsuario);

        login.presentacionLogin();        
    }
}
