package presentation;

import java.util.Date;
import java.util.Scanner;
class FormularioHuesped {

    private Scanner scanner;
    FormularioHuesped(Scanner scanner){
        this.scanner = scanner;
    }

    public String verificarNombre(){
        while(true){
            System.out.println("Ingrese el nombre del huésped:");
            String nombre = scanner.nextLine();
            if(!nombre.matches("[a-zA-Z]+")){
                System.out.println("Nombre inválido. Por favor, ingrese solo letras.");
            } else {
                return nombre;
            }
        }
    }

    public String verificarApellido(){
        while(true){
            System.out.println("Ingrese el apellido del huésped:");
            String apellido = scanner.nextLine();
            if(apellido.matches("[a-zA-Z]+")){
                return apellido;
            } else {
                System.out.println("Apellido inválido. Por favor, ingrese solo letras.");
            }
        }
    }

    public String verificarFechaDeNacimiento(){
        
        while(true){
            System.out.println("Ingrese la fecha de nacimiento del huésped (dd/mm/yyyy):");
            
        }
    }

    public String verificarNumeroDocumento(){
        System.out.println("Ingrese el número de documento del huésped: ");
    }

    public String verificarEmail(){
        System.out.println("Ingrese el email del huésped: ");


    }

    public String verificarNacionalidad(){
        System.out.println("Ingrese la nacionalidad del huésped: ");
    }

    public String verificarTelefono(){
        
        System.out.println("Ingrese el teléfono del huésped: ");
        
    }

    public String verificarCuit(){

        System.out.println("Ingrese el CUIT del huésped: ");

    }

    public String verificarOcupacion(){

        System.out.println("Ingrese la ocupación del huésped: ");

    }

    public IVA verificarPosicionFrenteAlIVA(){

        System.out.println("Ingrese la posición frente al IVA del huésped: ");

    }

    public TipoDocumento verificarTipoDocumento(){

        System.out.println("Ingrese el tipo de documento del huésped: ");

    }

    public String verfificarNumeroDocumento(){

        System.out.println("Ingrese el número de documento del huésped: ");

    }

    public Direccion verificarDireccion(String pais, String provincia, String ciudad, String calle, String numero, String piso, String departamento, String codigoPostal){

        System.out.println("Ingrese la dirección del huésped: ");
        
        System.out.println("País: " );
        pais = scanner.nextLine();  
        
        System.out.println("Provincia: " );
        provincia = scanner.nextLine();
        
        System.out.println("Ciudad: " );
        ciudad = scanner.nextLine();
        
        System.out.println("Calle: " );
        calle = scanner.nextLine();
        
        System.out.println("Número: " );
        numero = scanner.nextLine();
        
        System.out.println("Piso: " );
        piso = scanner.nextLine();
        
        System.out.println("Departamento: " );
        departamento = scanner.nextLine();
        
        System.out.println("Código Postal: " );
        codigoPostal = scanner.nextLine(); 
        

    }
    
    

    
}