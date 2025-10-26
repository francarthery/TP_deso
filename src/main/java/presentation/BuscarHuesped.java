package presentation;

import domain.Huesped;
import domain.TipoDocumento;

import java.util.List;
import java.util.Scanner;
import service.GestorHuesped;

public class BuscarHuesped {
    
    public Huesped buscar(GestorHuesped gestorHuesped, Scanner scanner) {
        System.out.println("----- Búsqueda de Huésped -----");
        FormularioHuesped formularioHuesped = new FormularioHuesped(scanner);

        System.out.print("Ingrese los nombres (o deje vacío para omitir): ");
        String nombres = formularioHuesped.verificarNombre(true);
        System.out.print("Ingrese el apellido (o deje vacío para omitir): ");
        String apellido = formularioHuesped.verificarApellido(true);
        System.out.print("Ingrese el tipo de documento (DNI, PASAPORTE, LE, LC, OTRO) o deje vacío para omitir: ");
        TipoDocumento tipoDocumento = formularioHuesped.verificarTipoDocumento(true);
        System.out.print("Ingrese el número de documento (o deje vacío para omitir): ");
        String numeroDocumento = formularioHuesped.verificarNumeroDocumento(true);

        try {
            List<Huesped> huespedes = gestorHuesped.buscarHuesped(apellido, nombres, tipoDocumento, numeroDocumento);
            int valorNumericoDeSeleccion = 1;
            
            if (huespedes.isEmpty()) {
                return ejecutarDarAltaHuesped();
            } 
            
            System.out.println("Huéspedes encontrados:");
            for(int i = 1; i <= huespedes.size(); i++){
                System.out.println(i + ". " + huespedes.get(i - 1).getNombres() + ' ' + huespedes.get(i - 1).getApellido() + " - " 
                                    + huespedes.get(i - 1).getTipoDocumento() + ": " + huespedes.get(i - 1).getNumeroDocumento());
            }
            
            do{
                System.out.println("Seleccione el huésped deseado por número (o vacío):");
                String seleccion = scanner.nextLine().trim();
                if(seleccion == null || seleccion.isEmpty()) return ejecutarDarAltaHuesped();
                
                try{
                    valorNumericoDeSeleccion = Integer.parseInt(seleccion);
                    if(valorNumericoDeSeleccion < 1 || valorNumericoDeSeleccion > huespedes.size()){
                        System.out.println("Selección inválida.");
                    }else{
                        return ejecutarModificarHuesped(huespedes.get(valorNumericoDeSeleccion - 1));
                    } 
                }catch(NumberFormatException e){
                    System.out.println("Selección inválida.");
                }
            }while(true);
            
        } catch (Exception e) {
            System.err.println("Error al buscar huéspedes: " + e.getMessage());
            return null; 
        }
        
    }

    //Dar alta huesped retorna una lista de huespedes. Modificar huesped uno solo
    //Se puede asi y todo tener un solo buscarHuesped? O tendremos que trabajar con 2 y listo?
    private Huesped ejecutarDarAltaHuesped(){
        DarAltaHuesped darAltaHuesped = new DarAltaHuesped();
        return darAltaHuesped.ejecutar(gestorHuesped, scanner); // ARREGLAR ESTO
    }

    private Huesped ejecutarModificarHuesped(Huesped huesped){
        ModificarHuesped modificarHuesped = new ModificarHuesped();
        return modificarHuesped.ejecutar(gestorHuesped, scanner, huesped);
    }
}