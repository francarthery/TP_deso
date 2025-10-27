package presentation;

import java.util.Scanner;
import service.GestorHuesped;
import domain.IVA;
import domain.TipoDocumento;
import java.time.LocalDate;
import domain.Huesped;

public class DarAltaHuesped {
    
    public Huesped darAltaHuesped(GestorHuesped gestorHuesped, Scanner scanner){
        System.out.println("====== Dar Alta Huésped =====");   
        System.out.print("¿Desea cargar un nuevo huésped al sistema? (S/N): ");
        String aceptacion = aceptacion(scanner);
        System.out.println();
        if(aceptacion.equals("N")){
            System.out.println("Operación cancelada. No se cargará ningún nuevo huésped.");
            System.out.println();
            return null;
        }
        FormularioHuesped formularioHuesped = new FormularioHuesped(scanner);
        String nombre = formularioHuesped.verificarNombre(false);
        String apellido = formularioHuesped.verificarApellido(false);
        LocalDate fechaNacimiento = formularioHuesped.verificarFechaNacimiento();
        TipoDocumento tipoDocumento = formularioHuesped.verificarTipoDocumento(false);
        String numeroDocumento = formularioHuesped.verificarNumeroDocumento(false, tipoDocumento);
        String cuit = null;
        if(tipoDocumento == TipoDocumento.DNI){
           cuit = formularioHuesped.verificarCuit();
        }
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

        outer:
        while(gestorHuesped.documentoExistente(tipoDocumento.name(), numeroDocumento)){
            System.out.println("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
            do{
                System.err.println("¿Desea aceptar igualmente o corregir? (Presione A si desea aceptar, C si desea corregir)");
                String eleccion = scanner.nextLine().trim();
                if(eleccion.equalsIgnoreCase("C")){
                    tipoDocumento = formularioHuesped.verificarTipoDocumento(false);
                    numeroDocumento = formularioHuesped.verificarNumeroDocumento(false, tipoDocumento);
                    break;
                }else if(eleccion.equalsIgnoreCase("A")){
                    break outer;
                }else{
                    System.out.println("Elección incorrecta");
                }
            }while(true);
        }

        Huesped huesped;
        huesped = gestorHuesped.darAltaHuesped(apellido, nombre, tipoDocumento, numeroDocumento, cuit, 
            posicionFrenteAlIVA, fechaNacimiento, telefono, email, ocupacion, nacionalidad, 
            pais, provincia, localidad, calle, numero, piso, departamento, codigoPostal);
        
        System.out.println("El huésped " + nombre + " " + apellido + " ha sido satisfactoriamente cargado al sistema.");
        return huesped;
    }

    public void ejecutar(GestorHuesped gestorHuesped, Scanner scanner) {
        String opcion;
        Huesped huesped;
        do {
            huesped = darAltaHuesped(gestorHuesped, scanner);
            if(huesped == null){
                return;
            }
            System.out.print("¿Desea cargar otro huésped? (S/N): ");
            opcion = aceptacion(scanner);
            opcion = opcion.toUpperCase();
        } while (opcion.equals("S"));
    }

   

    public String aceptacion(Scanner scanner){
        
        while(true){
            String eleccion = scanner.nextLine().trim().toUpperCase();
            if(eleccion.equals("S") || eleccion.equals("N")){
                return eleccion;
            }else{
                System.out.println("Elección incorrecta. Ingrese S para si desea cargar otro huésped o N para no.");
                System.out.print("Ingrese su elección (S/N): ");
            }
        }
    }
}
