package repository;

import domain.HuespedDTO;
import domain.TipoDocumento;
import java.util.List;
import exceptions.HuespedNoEncontradoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import domain.IVA;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class HuespedDAO {
    
    private static HuespedDAO instancia;

    private HuespedDAO() {}

    public static HuespedDAO getInstancia() {
        if (instancia == null) {
            instancia = new HuespedDAO();
        }
        return instancia;
    }

    public List<HuespedDTO> obtenerTodos() throws HuespedNoEncontradoException {
        
        List<HuespedDTO> huespedes = null;

        String rutaRecurso = "data/huespedes.csv";
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
                    
                    huespedes.add(new HuespedDTO(id, nombresBD, apellidoBD, tipoDocumentoBD, numeroDocumentoBD, cuitBD, posicionFrenteAlIVA, fechaDeNacimiento, telefono, emailBD, ocupacionBD, nacionalidadBD));
                
                } catch (IllegalArgumentException e) {
                    System.err.println("Error de parseo en CSV: " + linea + " | Error: " + e.getMessage());
                } catch (ParseException e) {
                    System.err.println("Error de parseo de fecha en CSV: " + linea + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo DAO: " + e.getMessage());
            e.printStackTrace();
        }


        return huespedes;
    }

    
}
