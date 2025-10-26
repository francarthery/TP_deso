package presentation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import domain.Direccion;
import java.time.format.DateTimeParseException;
import domain.IVA;
import domain.TipoDocumento;

class FormularioHuesped {

    private Scanner scanner;
    FormularioHuesped(Scanner scanner){
        this.scanner = scanner;
    }

    public String verificarNombre(){
        while(true){
            System.out.println("Ingrese el nombre del huésped:");
            String nombre = scanner.nextLine().trim();
            if(nombre.matches("\\p{L}+") && nombre.length() <= 50){
                return nombre;
            } else {
                System.out.println("El nombre solo debe contener letras. Por favor, ingrese solo letras.");
            }
        }
    }

    public String verificarApellido(){
        while(true){
            System.out.println("Ingrese el apellido del huésped:");
            String apellido = scanner.nextLine().trim();
            if(apellido.matches("\\p{L}+") && apellido.length() <= 50){
                return apellido;
            } else {
                System.out.println("El apellido solo debe contener letras. Por favor, ingrese solo letras.");
            }
        }
    }

    public LocalDate verificarFechaNacimiento(){
        
        while(true){
            System.out.println("Ingrese la fecha de nacimiento del huésped (dd/mm/yyyy):");

            String FORMATO_FECHA = "dd/MM/yyyy";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA);
            String fecha = scanner.nextLine().trim();
            try {
                LocalDate fechaNacimiento = LocalDate.parse(fecha, formatter);
                if(fechaNacimiento.isBefore(LocalDate.now())) {
                    return fechaNacimiento;
                } else {
                    System.out.println("La fecha de nacimiento debe ser anterior a la fecha actual. Por favor, ingrese una fecha válida.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Por favor, ingrese la fecha en el formato dd/mm/yyyy.");
            }
        }
    }

    public String verificarNumeroDocumento(){
        while(true){
            System.out.println("Ingrese el número de documento del huésped: ");
            String numeroDocumento = scanner.nextLine().trim();
            if(numeroDocumento.matches("\\d+") && numeroDocumento.length() <= 10){
                return numeroDocumento;
            } else {
                System.out.println("El número de documento debe contener solo dígitos. Por favor, ingrese solo números.");
            }
        }
    }

    public String verificarEmail(){
        while(true){
            System.out.println("Ingrese el email del huésped: ");
            String email = scanner.nextLine().trim();
            if(email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
                return email;
            } else {
                System.out.println("Email inválido. Por favor, ingrese un email válido.");
            }
        }
    }

    public String verificarNacionalidad(){
        while(true){
            System.out.println("Ingrese la nacionalidad del huésped:");
            String nacionalidad = scanner.nextLine().trim();
            if(nacionalidad.matches("[a-zA-Z ]+") && nacionalidad.length() <= 30){
                return nacionalidad;
            } else {
                System.out.println("La nacionalidad solo debe contener letras. Por favor, ingrese solo letras.");
            }
        }
    }

    public String verificarTelefono(){
        while(true){
            System.out.println("Ingrese el teléfono del huésped: ");
            String telefono = scanner.nextLine().trim();
            if(telefono.matches("\\+?\\d{7,15}")){
                return telefono;
            } else {
                System.out.println("El teléfono debe contener solo números. Por favor, ingrese un número de teléfono válido (7-15 dígitos, puede incluir '+').");
            }
        }
        
    }

    public String verificarCuit(){
        while(true){
            System.out.println("Ingrese el CUIT del huésped: ");
            String cuit = scanner.nextLine().trim();
            if(cuit.matches("\\d+") && cuit.length() <= 11){
                return cuit;
            } else {
                System.out.println("El CUIT debe contener solo dígitos. Por favor, ingrese solo números.");
            }
        }
    }

    public String verificarOcupacion(){
        while(true){
            System.out.println("Ingrese la ocupación del huésped: ");
            String ocupacion = scanner.nextLine().trim();
            if(ocupacion.matches("[a-zA-Z ]+") && ocupacion.length() <= 50){
                return ocupacion;
            } else {
                System.out.println("La ocupación solo debe contener letras. Por favor, ingrese solo letras.");
            }
        }
    }

    public IVA verificarPosicionFrenteAlIVA(){
        while(true){
            System.out.println("Ingrese la posición frente al IVA del huésped: ");
            String iva = scanner.nextLine().trim();
            try {
                IVA ivaPosicion = IVA.valueOf(iva.toUpperCase());
                return ivaPosicion;
            } catch (IllegalArgumentException e) {
                System.out.println("Posición frente al IVA inválida. Por favor, ingrese una posición válida. (RESPONSABLE_INSCRIPTO, MONOTRIBUTISTA, EXENTO, CONSUMIDOR_FINAL, NO_RESPONSABLE)");
            }
        }
    }

    public TipoDocumento verificarTipoDocumento(){    
        while(true){
            System.out.println("Ingrese el tipo de documento del huésped: ");
            String tipoDocumento = scanner.nextLine().trim();
            try {
                TipoDocumento tipoDoc = TipoDocumento.valueOf(tipoDocumento.toUpperCase());
                return tipoDoc;
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo de documento inválido. Por favor, ingrese un tipo de documento válido. (DNI, PASAPORTE, LE, LC, OTRO)");
            }
        }
    }

    public String verfificarNumeroDocumento(){
        
        while(true){
            System.out.println("Ingrese el número de documento del huésped: ");
            String numeroDocumento = scanner.nextLine().trim();
            if(numeroDocumento.matches("\\d+") && numeroDocumento.length() <= 10){
                return numeroDocumento;
            } else {
                System.out.println("El número de documento debe contener solo dígitos. Por favor, ingrese solo números.");
            }
        }
    }

    public String verificarPais(){
        while(true) {
            System.out.println("País: ");
            String pais = scanner.nextLine().trim();  

            if(!pais.matches("[a-zA-Z ]+") || pais.length() > 85){
                System.out.println("País inválido. Por favor, ingrese de nuevo.");
            } else {  
                return pais;
            }
        }
    }

    public String verificarProvincia(){
        while(true) {
            System.out.println("Provincia: ");
            String provincia = scanner.nextLine().trim();  

            if(!provincia.matches("[a-zA-Z ]+") || provincia.length() > 85){
                System.out.println("provincia inválida. Por favor, ingrese de nuevo.");
            } else {    
                return provincia;
            }
        }
    }

    public String verificarLocalidad(){
        while(true) {
            System.out.println("Localidad: ");
            String localidad = scanner.nextLine().trim();  

            if(!localidad.matches("[a-zA-Z ]+") || localidad.length() > 85){
                System.out.println("Localidad inválida. Por favor, ingrese de nuevo.");
            } else {    
                return localidad;
            }
        }
    }

    public String verificarCalle(){
        while(true) {
            System.out.println("Calle: ");
            String calle = scanner.nextLine().trim();  

            if(!calle.matches("[a-zA-Z0-9 ]+") || calle.length() > 56){
                System.out.println("Calle inválida. Por favor, ingrese de nuevo.");
            } else {    
                return calle;
            }
        }
    }

    public String verificarNumero(){
        while(true) {
            System.out.println("Número: ");
            String numero = scanner.nextLine().trim();  

            if(!numero.matches("\\d+") || numero.length() > 10){
                System.out.println("Número inválido. Por favor, ingrese de nuevo.");
            } else {    
                return numero;
            }
        }
    }
    
    public String verificarPiso() {
        while(true) {
            System.out.println("Piso: ");
            String piso = scanner.nextLine().trim();  

            if(piso.isEmpty() || (piso.matches("\\d+") && piso.length() <= 5)) {
        
                if (piso.isEmpty()) 
                    piso = null;
                
                return piso; 
            }   
            System.err.println("Piso inválido. Por favor, ingrese de nuevo.");
        }        
    }
    
    public String verificarDepartamento(){
        while (true) {
            System.out.print("Departamento: ");
            String departamento = scanner.nextLine().trim();

            if (departamento.isEmpty() || (departamento.matches("[a-zA-Z0-9 ]+") && departamento.length() <= 5)) {
                if (departamento.isEmpty()) 
                    departamento = null;
                return departamento; 
            }
            System.err.println("Departamento inválido. Por favor, ingrese de nuevo.");
        }
    }

    public String verificarCodigoPostal(){
        while(true) {
            System.out.println("Código Postal: " );
            String codigoPostal = scanner.nextLine().trim();  

            if(!codigoPostal.matches("\\d+") || codigoPostal.length() > 10){
                System.out.println("Código Postal inválido. Por favor, ingrese de nuevo.");
            } else {    
                return codigoPostal;
            }
        }
    }
    
}