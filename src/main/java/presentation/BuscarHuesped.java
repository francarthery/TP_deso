package presentation;

import domain.Huesped;
import domain.TipoDocumento;

import java.util.List;
import java.util.Scanner;
import service.GestorHuesped;

class BuscarHuesped {
    
    public List<Huesped> buscar(GestorHuesped gestorHuesped, Scanner scanner) {
        System.out.println("----- Búsqueda de Huésped -----");
        

        System.out.print("Ingrese los nombres (o deje vacío para omitir): ");
        String nombres = scanner.nextLine();
        System.out.print("Ingrese el apellido (o deje vacío para omitir): ");
        String apellido = scanner.nextLine();
        System.out.print("Ingrese el tipo de documento (DNI, PASAPORTE, LE, LC, OTRO) o deje vacío para omitir: ");
        String tipoDocumentoInput = scanner.nextLine();
        TipoDocumento tipoDocumento = tipoDocumentoInput.isEmpty() ? null : TipoDocumento.valueOf(tipoDocumentoInput.toUpperCase());
        System.out.print("Ingrese el número de documento (o deje vacío para omitir): ");
        String numeroDocumento = scanner.nextLine();

        try {
            List<Huesped> huespedes = gestorHuesped.buscarHuesped(apellido, nombres, tipoDocumento, numeroDocumento);
            if (huespedes.isEmpty()) {
                System.out.println("No se encontraron huéspedes en el sistema.");
                //pasa a ejecutar CU11
            } else {
                System.out.println("Huéspedes encontrados:");
                for(int i = 1; i <= huespedes.size(); i++){
                    System.out.println(i + ". " + huespedes.get(i - 1).getNombres() + ' ' + huespedes.get(i - 1).getApellido() + " - " 
                                        + huespedes.get(i - 1).getTipoDocumento() + ": " + huespedes.get(i - 1).getNumeroDocumento());
                }
                
                boolean sinSeleccion = true;
                while(sinSeleccion){
                    System.out.println("Seleccione el huésped deseado por número (o vacío):");
                    String seleccion = scanner.nextLine();
                    if(seleccion == null || seleccion.isEmpty()){
                        System.out.println("CU11");
                        sinSeleccion = false;
                    }else{
                        try{
                            int valorNumericoDeSeleccion = Integer.parseInt(seleccion);
                            if(valorNumericoDeSeleccion < 1 || valorNumericoDeSeleccion > huespedes.size()){
                                System.out.println("Selección inválida.");
                            }else{
                                //Ejecuta CU10
                                sinSeleccion = false;
                                System.out.println("CU10");
                            } 
                        }catch(NumberFormatException e){
                            System.out.println("Selección inválida.");
                        }
                    }
                }
            }
            return huespedes;
        } catch (Exception e) {
            System.err.println("Error al buscar huéspedes: " + e.getMessage());
            return List.of(); 
        }
        
    }
}