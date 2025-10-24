package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import domain.Huesped;
import domain.IVA;
import domain.TipoDocumento;
import exceptions.HuespedNoEncontradoException;


public class HuespedDAO {
    
    private static HuespedDAO instancia;
    private static final DateTimeFormatter LocalDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static String rutaRecurso = "data/huespedes.csv";
    private static ClassLoader classLoader = HuespedDAO.class.getClassLoader(); //Preguntar si es correcto
    
    private HuespedDAO() {}

    public static HuespedDAO getInstancia() {
        if (instancia == null) {
            instancia = new HuespedDAO();
        }
        return instancia;
    }

    public List<Huesped> obtenerTodos() throws HuespedNoEncontradoException {
        
        List<Huesped> huespedes = new ArrayList<>();
        InputStream inputStream = classLoader.getResourceAsStream(rutaRecurso);

        if (inputStream == null) {
            System.err.println("Error: No se pudo encontrar el archivo " + rutaRecurso);
            return huespedes;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                
                if (datos.length < 12) continue; 

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
            huesped.getNacionalidad()
        );

        try (PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter(rutaRecurso, true)))) {
            out.println(nuevaLinea); 
            return true; 
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo DAO: " + e.getMessage());
            return false;
        }
    }

    public boolean documentoExistente(String documento){
        InputStream inputStream = classLoader.getResourceAsStream(rutaRecurso);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                
                if (datos.length < 12) continue; 

                try {
                    String documentoBD = datos[4];
                    if(documentoBD.equalsIgnoreCase(documento)) return true;
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
