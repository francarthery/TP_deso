package presentation;

import java.util.Scanner;

import domain.Huesped;
import service.GestorHuesped;
import service.GestorEstadia;

public class DarBajaHuesped {

    public void darBajaHuesped(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia, Huesped huesped, Scanner scanner){
        if(gestorEstadia.tieneEstadia(huesped.getId())){
            System.out.println("El huésped no puede ser eliminado pues se ha alojado en el Hotel en alguna oportunidad. PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
            String tecla = scanner.nextLine();
        }else{
            System.out.println("Los datos del huésped " + huesped.getNombres() + " " + huesped.getApellido() + ", " 
                + huesped.getTipoDocumento() + " " + huesped.getNumeroDocumento() + " serán sido eliminados del sistema.");
            
            String eleccion = aceptacion(scanner);

            if(eleccion.equals("ELIMINAR")){
                if(gestorHuesped.darBajaHuesped(huesped)){
                    System.out.println("Los datos del huésped " + huesped.getNombres() + " " + huesped.getApellido() + ", " 
                        + huesped.getTipoDocumento() + " " + huesped.getNumeroDocumento() + " han sido eliminados del sistema.");
                    System.out.println("PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
                    String tecla = scanner.nextLine();
                }else{
                    System.out.println("No ha sido posible eliminar al huésped: " + huesped.getNombres() + " " + huesped.getApellido() + ".");
                }
            }
        }
    }

    public String aceptacion(Scanner scanner){
        String eleccion;
        do{
            System.out.println("Presione ELIMINAR si desea borrar al huésped o CANCELAR si no lo desea.");
            eleccion = scanner.nextLine();
            if(eleccion.equals("ELIMINAR") || eleccion.equals("CANCELAR")){
                return eleccion;
            }else{
                System.out.println("La entrada ingresada es inválida.");
            }
        }while(true);
    }
}