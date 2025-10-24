package presentation;

import presentation.BuscarHuesped;
import domain.Huesped;
import domain.HuespedDTO;
import service.GestorHuesped;

import java.util.Scanner;
import java.util.List;

public class MenuPrincipal {
    
    public void menuPrincipal(Scanner scanner) {
        System.out.println("======= Menú Principal =======");
        System.out.println("1. Opción: Buscar Huésped");
        System.out.println("2. Opción: Dar de alta Huésped");
        System.out.println("3. Opción: Modificar Huésped");
        System.out.println("4. Opción: Dar de baja Huésped");
        System.out.println("5. Salir");
        
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        
        GestorHuesped gestorHuesped = GestorHuesped.getInstancia(); 

        switch (opcion) {
            case 1:
                BuscarHuesped buscarHuesped = new BuscarHuesped();
                List<Huesped> huespedesEncontrados = buscarHuesped.buscar(gestorHuesped, scanner);
                break;
            case 2:
                DarAltaHuesped darAltaHuesped = new DarAltaHuesped();
                
                break;
            case 3:
                // Lógica para la opción 3
                break;
            case 4:  
                // Lógica para la opción 4
                break;
            case 5:
                System.out.println("Saliendo del programa. ¡Hasta luego!");
            default:
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
                menuPrincipal(scanner);
                break;
        }
    }

}
