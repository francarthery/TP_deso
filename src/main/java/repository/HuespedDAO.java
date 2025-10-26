package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import domain.Direccion;
import domain.Huesped;
import domain.IVA;
import domain.TipoDocumento;
import exceptions.HuespedNoEncontradoException;


public class HuespedDAO {
    
    private static HuespedDAO instancia;
    private static final DateTimeFormatter LocalDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String RUTA_ARCHIVO = "src/main/resources/data/huespedes.csv";
    
    private HuespedDAO() {}

    public static HuespedDAO getInstancia() {
        if (instancia == null) {
            instancia = new HuespedDAO();
        }
        return instancia;
    }

    public List<Huesped> obtenerTodos() throws HuespedNoEncontradoException {
        List<Huesped> huespedes = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                
                if (datos.length < 20) continue; 

                try {
                    int id = Integer.parseInt(datos[0]);
                    String nombresBD = datos[1];
                    String apellidoBD = datos[2];
                    TipoDocumento tipoDocumentoBD = TipoDocumento.valueOf(datos[3]);
                    String numeroDocumentoBD = datos[4];
                    String cuitBD = datos[5];
                    IVA posicionFrenteAlIVA = IVA.valueOf(datos[6]);
                    LocalDate fechaDeNacimiento = LocalDate.parse(datos[7]);
                    String telefono = datos[8];
                    String emailBD = datos[9];
                    String ocupacionBD = datos[10];
                    String nacionalidadBD = datos[11];
                    String paisBD = datos[12];
                    String provinciaBD = datos[13];
                    String ciudadBD = datos[14];
                    String calleBD = datos[15];
                    String numeroBD = datos[16];
                    String pisoBD = datos[17];
                    String departamentoBD = datos[18];
                    String codigoPostalBD = datos[19];

                    Direccion direccion = new Direccion.Builder()
                        .pais(paisBD)
                        .provincia(provinciaBD)
                        .localidad(ciudadBD)
                        .calle(calleBD)
                        .numero(numeroBD)
                        .piso(pisoBD)
                        .departamento(departamentoBD)
                        .codigoPostal(codigoPostalBD)
                        .build(); 
                    
                    Huesped huesped = new Huesped.Builder()
                        .nombres(nombresBD)
                        .apellido(apellidoBD)
                        .tipoDocumento(tipoDocumentoBD)
                        .numeroDocumento(numeroDocumentoBD)
                        .cuit(cuitBD)
                        .posicionFrenteAlIVA(posicionFrenteAlIVA)
                        .fechaDeNacimiento(fechaDeNacimiento)
                        .telefono(telefono)
                        .email(emailBD)
                        .ocupacion(ocupacionBD)
                        .nacionalidad(nacionalidadBD)
                        .direccion(direccion)
                        .build();
                    huespedes.add(huesped);
                
                } catch (IllegalArgumentException e) {
                    System.err.println("Error de parseo en CSV: " + linea + " | Error: " + e.getMessage());
                } catch (DateTimeParseException e) {
                    System.err.println("Error de parseo de fecha en CSV: " + linea + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo DAO: " + e.getMessage());
        }

        return huespedes;
    }

    public boolean agregarHuesped(Huesped huesped) {

        String fechaFormateada = LocalDateFormat.format(huesped.getFechaDeNacimiento());
        
        String nuevaLinea = String.join(",",
            String.valueOf(huesped.getId()),
            huesped.getNombres(),
            huesped.getApellido(),
            huesped.getTipoDocumento().name(),
            huesped.getNumeroDocumento(),
            huesped.getCuit(),
            huesped.getPosicionFrenteAlIVA().name(),
            fechaFormateada,
            huesped.getTelefono(),
            huesped.getEmail(),
            huesped.getOcupacion(),
            huesped.getNacionalidad(),
            huesped.getDireccion().getPais(),
            huesped.getDireccion().getProvincia(),
            huesped.getDireccion().getLocalidad(),
            huesped.getDireccion().getCalle(),
            huesped.getDireccion().getNumero(),
            huesped.getDireccion().getPiso(),
            huesped.getDireccion().getDepartamento(),
            huesped.getDireccion().getCodigoPostal()  
        );

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true)))) {
            out.println(nuevaLinea); 
            return true; 
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean documentoExistente(String tipoDocumento, String documento){
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                
                if (datos.length < 20) continue; 

                try {
                    String tipoDocumentoBD = datos[3];
                    String documentoBD = datos[4];
                    if(tipoDocumentoBD.equalsIgnoreCase(tipoDocumento) && documentoBD.equalsIgnoreCase(documento)) return true;
                } catch (IllegalArgumentException e) {
                    System.err.println("Error de parseo en CSV: " + linea + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo DAO: " + e.getMessage());
        }
        return false;
    }
}
