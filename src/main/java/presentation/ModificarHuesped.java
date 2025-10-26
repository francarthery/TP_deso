package presentation;
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
    
    public Huesped ejecutar(GestorHuesped gestorHuesped, Scanner scanner, Huesped huesped){
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

        return null;
    }

    public void modificacionNombres(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Nombres: " + huesped.getNombres() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setNombres(formularioHuesped.verificarNombre(false));
        }
    }
    
    public void modificarApellido(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Apellido: " + huesped.getApellido() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setApellido(formularioHuesped.verificarApellido(false));
        }
       
    }

    public void modificarTipoDocumento(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Tipo de Documento: " + huesped.getTipoDocumento() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setTipoDocumento(formularioHuesped.verificarTipoDocumento(false));
        }
    }

    public void modificarNumeroDocumento(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Número de Documento: " + huesped.getNumeroDocumento() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setNumeroDocumento(formularioHuesped.verificarNumeroDocumento(false));
        }
    }

    public void modificarCuit(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("CUIT: " + huesped.getCuit() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setCuit(formularioHuesped.verificarCuit());
        }
    }

    public void modificarPoscicionFrenteAlIVA(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Posición frente al IVA: " + huesped.getPosicionFrenteAlIVA() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setPosicionFrenteAlIVA(formularioHuesped.verificarPosicionFrenteAlIVA());
        }
    }

    public void modificarFechaNacimiento(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Fecha de Nacimiento: " + huesped.getFechaDeNacimiento() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setFechaDeNacimiento(formularioHuesped.verificarFechaNacimiento());
        }
    }
    
    public void modificarTelefono(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Teléfono: " + huesped.getTelefono() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setTelefono(formularioHuesped.verificarTelefono());
        }
    }

    public void modificarEmail(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Email: " + huesped.getEmail() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setEmail(formularioHuesped.verificarEmail());
        }
    }

    public void modificarOcupacion(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Ocupación: " + huesped.getOcupacion() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setOcupacion(formularioHuesped.verificarOcupacion());
        }
    }
    
    public void modificarNacionalidad(Huesped huesped, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Nacionalidad: " + huesped.getNacionalidad() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setNacionalidad(formularioHuesped.verificarNacionalidad());
        }
        
    }
    
    public void modificarPais(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("País: " + direccion.getPais() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            direccion.setPais(formularioHuesped.verificarPais());
        }
    }
    
    public void modificarProvincia(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Provincia: " + direccion.getProvincia() + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            direccion.setProvincia(formularioHuesped.verificarProvincia());
        }
    }
    
    public vo modificarLocalidad(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        System.out.println("Localidad: " + direccion. + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            direccion.setLocalidad(formularioHuesped.verificarLocalidad());
        }
        return localidad;
    }
    
    public String modificarCalle(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        String calle = huesped.getDireccion().getCalle();
        System.out.println("Calle: " + calle + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            direccion.setCalle(formularioHuesped.verificarCalle())
        return calle;
    }
    
    public void modificarNumero(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        String numero = direccion.getNumero();
        System.out.println("Número: " + numero + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setNumero();
        }
    
    }

    public void modificarPiso(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        String piso = direccion.getPiso();
        System.out.println("Piso: " + piso + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setPiso() = formularioHuesped.verificarPiso();
        }
       
    }
    
    public void modificarDepartamento(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        String departamento = huesped.getDireccion().getDepartamento();
        System.out.println("Departamento: " + departamento + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            huesped.setDepartamento = formularioHuesped.verificarDepartamento();
        }
        
    }

    public void modificarCodigoPostal(Direccion direccion, Scanner scanner, FormularioHuesped formularioHuesped){
        String codigoPostal = huesped.getDireccion().getCodigoPostal();
        System.out.println("Código Postal: " + codigoPostal + " Desea modificarlo? (S/N)");
        String eleccion = aceptacion(scanner);
        if(eleccion.equals("S")){
            codigoPostal = formularioHuesped.verificarCodigoPostal();
        }
        huesped.setCodigoPostal(codigoPostal);
    }

    public String aceptacion(Scanner scanner){
        do{
            String eleccion = scanner.nextLine().trim().toUpperCase();
            if(eleccion.equals("S") || eleccion.equals("N")){
                return eleccion;
            }else{
                System.out.println("Entrada inválida. Por favor ingrese 'S' para Sí o 'N' para No.");
            }
        }while(true);
    }



}

