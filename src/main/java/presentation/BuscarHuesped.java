package presentation;

import domain.Huesped;
import domain.TipoDocumento;
import service.GestorEstadia;

import java.util.List;
import java.util.Scanner;
import service.GestorHuesped;

public class BuscarHuesped {
    
    public Huesped buscar(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia, Scanner scanner) {
        System.out.println("----- Búsqueda de Huésped -----");
        FormularioHuesped formularioHuesped = new FormularioHuesped(scanner);

        String nombres = formularioHuesped.verificarNombre(true);
        String apellido = formularioHuesped.verificarApellido(true);
        TipoDocumento tipoDocumento = formularioHuesped.verificarTipoDocumento(true);
        String numeroDocumento = formularioHuesped.verificarNumeroDocumento(true);

        try {
            List<Huesped> huespedes = gestorHuesped.buscarHuesped(apellido, nombres, tipoDocumento, numeroDocumento);
            int valorNumericoDeSeleccion = 1;
            
            if (huespedes.isEmpty()) {
                return ejecutarDarAltaHuesped(gestorHuesped, scanner);
            } 
            
            System.out.println("Huéspedes encontrados:");
            for(int i = 1; i <= huespedes.size(); i++){
                System.out.println(i + ". " + huespedes.get(i - 1).getNombres() + ' ' + huespedes.get(i - 1).getApellido() + " - " 
                                    + huespedes.get(i - 1).getTipoDocumento() + ": " + huespedes.get(i - 1).getNumeroDocumento());
            }
            
            do{
                System.out.println("Seleccione el huésped deseado por número (o vacío):");
                String seleccion = scanner.nextLine().trim();
                if(seleccion == null || seleccion.isEmpty()) return ejecutarDarAltaHuesped(gestorHuesped, scanner);
                
                try{
                    valorNumericoDeSeleccion = Integer.parseInt(seleccion);
                    if(valorNumericoDeSeleccion < 1 || valorNumericoDeSeleccion > huespedes.size()){
                        System.out.println("Selección inválida.");
                    }else{
                        return ejecutarModificarHuesped(gestorHuesped, gestorEstadia, scanner, huespedes.get(valorNumericoDeSeleccion - 1));
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

    private Huesped ejecutarDarAltaHuesped(GestorHuesped gestorHuesped, Scanner scanner){
        DarAltaHuesped darAltaHuesped = new DarAltaHuesped();
        return darAltaHuesped.darAltaHuesped(gestorHuesped, scanner);
    }

    private Huesped ejecutarModificarHuesped(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia, Scanner scanner, Huesped huesped){
        ModificarHuesped modificarHuesped = new ModificarHuesped();
        return modificarHuesped.ejecutar(gestorHuesped, gestorEstadia, scanner, huesped);
    }
}