package presentation;

import domain.Huesped;
import service.GestorEstadia;
import service.GestorHuesped;
import java.util.InputMismatchException;

import java.util.Scanner;

public class MenuPrincipal {

    private final GestorHuesped gestorHuesped;
    private final GestorEstadia gestorEstadia;

    public MenuPrincipal(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia) {
        this.gestorHuesped = gestorHuesped;
        this.gestorEstadia = gestorEstadia;
    }

    public void menuPrincipal(Scanner scanner) {
        int opcion = -1;

        while(opcion != 3){
            System.out.println("======= Menú Principal =======");
            System.out.println("1. Opción: Buscar Huésped");
            System.out.println("2. Opción: Dar de alta Huésped");
            System.out.println("3. Salir");
            System.out.println("==============================");
            System.out.print("Ingrese una opción: ");
            try{
                opcion = scanner.nextInt();
                System.out.println();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        BuscarHuesped buscarHuesped = new BuscarHuesped();
                        Huesped huespedEncontrado = buscarHuesped.buscar(gestorHuesped, gestorEstadia, scanner);
                        break;
                    case 2:
                        DarAltaHuesped darAltaHuesped = new DarAltaHuesped();
                        darAltaHuesped.ejecutar(gestorHuesped, scanner);
                        break;
                    case 3:
                        System.out.println("Saliendo del programa. ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");
                        menuPrincipal(scanner);
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }
}
