package presentation;

import java.util.Scanner;
import service.GestorHuesped;
import domain.IVA;
import domain.TipoDocumento;
import java.time.LocalDate;
import domain.Direccion;

public class DarAltaHuesped {
    
    public void darAltaHuesped(GestorHuesped gestorHuesped, Scanner scanner){
        System.out.println("----- Cargar Huésped -----");   
        
        FormularioHuesped formularioHuesped = new FormularioHuesped(scanner);
        String nombre = formularioHuesped.verificarNombre();
        String apellido = formularioHuesped.verificarApellido();
        LocalDate fechaNacimiento = formularioHuesped.verificarFechaNacimiento();
        TipoDocumento tipoDocumento = formularioHuesped.verificarTipoDocumento();
        String numeroDocumento = formularioHuesped.verificarNumeroDocumento();
        String cuit = formularioHuesped.verificarCuit();
        IVA posicionFrenteAlIVA = formularioHuesped.verificarPosicionFrenteAlIVA();
        String telefono = formularioHuesped.verificarTelefono(); 
        String email = formularioHuesped.verificarEmail();
        String ocupacion = formularioHuesped.verificarOcupacion();
        String nacionalidad = formularioHuesped.verificarNacionalidad();
        String pais = formularioHuesped.verificarPais();
        String provincia = formularioHuesped.verificarProvincia();
        String localidad = formularioHuesped.verificarLocalidad();
        String calle = formularioHuesped.verificarCalle();
        String numero = formularioHuesped.verificarNumero();
        String piso = formularioHuesped.verificarPiso();
        String departamento = formularioHuesped.verificarDepartamento();
        String codigoPostal = formularioHuesped.verificarCodigoPostal();

        while(gestorHuesped.documentoExistente(numeroDocumento)){
            System.out.println("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
            do{
                System.err.println("¿Desea aceptar igualmente o corregir? (Presione A si desea aceptar, C si desea corregir)");
                String eleccion = scanner.nextLine().trim();
                if(eleccion.equalsIgnoreCase("C")){
                    numeroDocumento = formularioHuesped.verificarNumeroDocumento();
                    tipoDocumento = formularioHuesped.verificarTipoDocumento();
                    break;
                }else if(eleccion.equalsIgnoreCase("A")){
                    break;
                }else{
                    System.out.println("Eleccion incorrecta");
                }
            }while(true);
        }

        gestorHuesped.darAltaHuesped(apellido, nombre, tipoDocumento, numeroDocumento, cuit, 
            posicionFrenteAlIVA, fechaNacimiento, telefono, email, ocupacion, nacionalidad, 
            pais, provincia, localidad, calle, numero, piso, departamento, codigoPostal);
        
        System.out.println("El huéped" + nombre + " " + apellido + "ha sido satisfactoriamente cargado al sistema.");

    }

    public void ejecutar(GestorHuesped gestorHuesped, Scanner scanner) {
        String opcion;
        do {
            darAltaHuesped(gestorHuesped, scanner);
            System.out.print("¿Desea cargar otro huésped? (S/N): ");
            opcion = scanner.nextLine().trim().toUpperCase();
        } while (opcion.equals("S"));
    }


    
   
    
}
