package presentation;

import domain.HuespedDTO;
import domain.TipoDocumento;

import java.util.List;
import java.util.Scanner;
import service.GestorHuesped;

class BuscarHuesped {
    
    public List<HuespedDTO> buscar(GestorHuesped gestorHuesped, Scanner scanner) {
        System.out.println("----- Búsqueda de Huésped -----");
        

        System.out.print("Ingrese el apellido (o deje vacío para omitir): ");
        String apellido = scanner.nextLine();
        System.out.print("Ingrese los nombres (o deje vacío para omitir): ");
        String nombres = scanner.nextLine();
        System.out.print("Ingrese el tipo de documento (DNI, PASAPORTE, LE, LC, OTRO) o deje vacío para omitir: ");
        TipoDocumento tipoDocumento = scanner.nextLine().isEmpty() ? null : TipoDocumento.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("Ingrese el número de documento (o deje vacío para omitir): ");
        String numeroDocumento = scanner.nextLine();
        
        try {
            List<HuespedDTO> huespedes = gestorHuesped.buscarHuesped(apellido, nombres, tipoDocumento, numeroDocumento);
            if (huespedes.isEmpty()) {
                System.out.println("No se encontraron huéspedes en el sistema.");
                //pasa a ejecutar CU11
            } else {
                System.out.println("Huéspedes encontrados:");
                for (HuespedDTO huesped : huespedes) {
                    System.out.println(huesped);
                }
                
            }
            return huespedes;
        } catch (Exception e) {
            System.err.println("Error al buscar huéspedes: " + e.getMessage());
            return null;
        }
        
    }
}