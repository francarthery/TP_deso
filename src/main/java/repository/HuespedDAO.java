package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import domain.Huesped;
import domain.IVA;
import domain.TipoDocumento;
import exceptions.HuespedNoEncontradoException;


public class HuespedDAO {
    
    private static HuespedDAO instancia;
    private static  final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private HuespedDAO() {}

    public static HuespedDAO getInstancia() {
        if (instancia == null) {
            instancia = new HuespedDAO();
        }
        return instancia;
    }

    public List<Huesped> obtenerTodos() throws HuespedNoEncontradoException {
        
        List<Huesped> huespedes = new ArrayList<>();

        String rutaRecurso = "data/huespedes.csv";
        

        ClassLoader classLoader = getClass().getClassLoader();
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
                    Date fechaDeNacimiento = dateFormat.parse(datos[7]);
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
                } catch (ParseException e) {
                    System.err.println("Error de parseo de fecha en CSV: " + linea + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo DAO: " + e.getMessage());
        }

        return huespedes;
    }

    public boolean agregarHuesped(Huesped huesped) {

        String fechaFormateada = dateFormat.format(huesped.getFechaDeNacimiento());
            
        String rutaRecurso= "data/huespedes.csv";

        String nuevaLinea = String.join(",",
            String.valueOf(huesped.getId()), // Asumimos que el ID ya viene en el DTO
            huesped.getNombres(),
            huesped.getApellido(),
            huesped.getTipoDocumento().name(), // .name() convierte el Enum a String
            huesped.getNumeroDocumento(),
            huesped.getCuit(),
            huesped.getPosicionFrenteAlIVA().name(),
            fechaFormateada,
            huesped.getTelefono(),
            huesped.getEmail(),
            huesped.getOcupacion(),
            huesped.getNacionalidad()
        );

        
        //    Usamos try-with-resources para que todo se cierre solo.
        //    El 'true' en FileWriter significa MODO APPEND (agregar al final)
        try (FileWriter fw = new FileWriter(rutaRecurso, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) 
        {
            out.println(nuevaLinea); // Escribimos la línea y un salto de línea
            return true; // Éxito
            
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo DAO: " + e.getMessage());
            return false; // Fracaso
        }
    }
}
