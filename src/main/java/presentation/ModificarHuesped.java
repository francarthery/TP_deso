package presentation;
import service.GestorEstadia;
import service.GestorHuesped;
import domain.Huesped;

import presentation.FormularioHuesped;
import java.text.Normalizer.Form;
import java.util.Scanner;
import domain.Direccion;
import java.time.LocalDate;
import domain.TipoDocumento;
import domain.IVA;

public class ModificarHuesped {
    
    public Huesped ejecutar(GestorHuesped gestorHuesped, GestorEstadia gestorEstadia, Scanner scanner, Huesped huesped){
        System.out.println("===== Modificación de Huésped =====");
        System.out.println("Desea modificar los datos del huésped: " + huesped.getNombres() + " " + huesped.getApellido() + " ? SIGUIENTE / CANCELAR / BORRAR");
        do{ 
            System.out.print("Ingrese su elección: ");
            String eleccion = scanner.nextLine().trim().toUpperCase();
            System.out.println();
            if(eleccion.equals("SIGUIENTE")) break;
            else if(eleccion.equals("CANCELAR")){
                System.out.println("Modificación cancelada.");
                return huesped;
            }else if(eleccion.equals("BORRAR")){
                DarBajaHuesped darBajaHuesped = new DarBajaHuesped();
                darBajaHuesped.darBajaHuesped(gestorHuesped, gestorEstadia, huesped, scanner);
                return null;
            }else{
                System.out.println("Entrada inválida. Por favor ingrese SIGUIENTE si desea modificar, CANCELAR si no desea modificar o BORRAR si desea eliminar el huésped.");
            }
        }while(true);

        FormularioHuesped formularioHuesped = new FormularioHuesped(scanner);
        modificacionNombres(huesped, scanner, formularioHuesped);
        modificarApellido(huesped, scanner, formularioHuesped);
        modificarTipoDocumento(huesped, scanner, formularioHuesped);
        modificarNumeroDocumento(huesped, scanner, formularioHuesped);
        modificarCuit(huesped, scanner, formularioHuesped);
        modificarPoscicionFrenteAlIVA(huesped, scanner, formularioHuesped);
        modificarFechaNacimiento(huesped, scanner, formularioHuesped);
        modificarTelefono(huesped, scanner, formularioHuesped);
        modificarEmail(huesped, scanner, formularioHuesped);
        modificarOcupacion(huesped, scanner, formularioHuesped);
        modificarNacionalidad(huesped, scanner, formularioHuesped);
        
        Direccion direccion = huesped.getDireccion();
        modificarPais(direccion, scanner, formularioHuesped);
        modificarProvincia(direccion, scanner, formularioHuesped);
        modificarLocalidad(direccion, scanner, formularioHuesped);
        modificarCalle(direccion, scanner, formularioHuesped);
        modificarNumero(direccion, scanner, formularioHuesped);
        modificarPiso(direccion, scanner, formularioHuesped);
        modificarDepartamento(direccion, scanner, formularioHuesped);
        modificarCodigoPostal(direccion, scanner, formularioHuesped);

        if(gestorHuesped.modificarHuesped(huesped)) return huesped;
        else{
            System.out.println("No se pudo modificar el huésped.");
            return null;
        }
    }

    public void modificacionNombres(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Nombres: " + huesped.getNombres() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setNombres(formularioHuesped.verificarNombre(false));
        }
    }
    
    public void modificarApellido(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Apellido: " + huesped.getApellido() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setApellido(formularioHuesped.verificarApellido(false));
        }
       
    }

    public void modificarTipoDocumento(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Tipo de Documento: " + huesped.getTipoDocumento() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();        
        if(eleccion.equals("S")){
            huesped.setTipoDocumento(formularioHuesped.verificarTipoDocumento(false));
        }
    }

    public void modificarNumeroDocumento(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Número de Documento: " + huesped.getNumeroDocumento() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setNumeroDocumento(formularioHuesped.verificarNumeroDocumento(false, huesped.getTipoDocumento()));
        }
    }

    public void modificarCuit(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("CUIT: " + huesped.getCuit() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setCuit(formularioHuesped.verificarCuit());
        }
    }

    public void modificarPoscicionFrenteAlIVA(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Posición frente al IVA: " + huesped.getPosicionFrenteAlIVA() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setPosicionFrenteAlIVA(formularioHuesped.verificarPosicionFrenteAlIVA());
        }
    }

    public void modificarFechaNacimiento(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Fecha de Nacimiento: " + huesped.getFechaDeNacimiento() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setFechaDeNacimiento(formularioHuesped.verificarFechaNacimiento());
        }
    }
    
    public void modificarTelefono(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Teléfono: " + huesped.getTelefono() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setTelefono(formularioHuesped.verificarTelefono());
        }
    }

    public void modificarEmail(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Email: " + huesped.getEmail() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setEmail(formularioHuesped.verificarEmail());
        }
    }

    public void modificarOcupacion(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Ocupación: " + huesped.getOcupacion() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setOcupacion(formularioHuesped.verificarOcupacion());
        }
    }
    
    public void modificarNacionalidad(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Nacionalidad: " + huesped.getNacionalidad() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            huesped.setNacionalidad(formularioHuesped.verificarNacionalidad());
        }
        
    }
    
    public void modificarPais(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("País: " + direccion.getPais() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setPais(formularioHuesped.verificarPais());
        }
    }
    
    public void modificarProvincia(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Provincia: " + direccion.getProvincia() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setProvincia(formularioHuesped.verificarProvincia());
        }
    }
    
    public void modificarLocalidad(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Localidad: " + direccion.getLocalidad() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setLocalidad(formularioHuesped.verificarLocalidad());
        }
    }
    
    public void modificarCalle(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Calle: " + direccion.getCalle() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setCalle(formularioHuesped.verificarCalle());
        }
    }
    
    public void modificarNumero(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Número: " + direccion.getNumero() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setNumero(formularioHuesped.verificarNumero());
        }
    }

    public void modificarPiso(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Piso: " + direccion.getPiso() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setPiso(formularioHuesped.verificarPiso());
        }
       
    }
    
    public void modificarDepartamento(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Departamento: " + direccion.getDepartamento() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setDepartamento(formularioHuesped.verificarDepartamento());
        }
        
    }

    public void modificarCodigoPostal(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.print("Código Postal: " + direccion.getCodigoPostal() + " ¿Desea modificarlo? (S/N): ");
        String eleccion = aceptacion(scanner);
        System.out.println();
        if(eleccion.equals("S")){
            direccion.setCodigoPostal(formularioHuesped.verificarCodigoPostal());
        }
    }

    public String aceptacion(Scanner scanner){
        do{
            String eleccion = scanner.nextLine().trim().toUpperCase();
            if(eleccion.equals("S") || eleccion.equals("N")){
                return eleccion;
            }else{
                System.out.println("Entrada inválida. Por favor ingrese 'S' para Sí o 'N' para No.");
                System.out.print("Reingrese su elección: ");
            }
        }while(true);
    }



}

