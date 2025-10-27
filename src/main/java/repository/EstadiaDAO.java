package repository;

import domain.Estadia;
import domain.Huesped;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EstadiaDAO {
    private static EstadiaDAO instancia;
    private static final DateTimeFormatter LocalDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String RUTA_ESTADIAS = "src/main/resources/data/estadias.csv";
    private static final String RUTA_HUESPEDES_ESTADIAS = "src/main/resources/data/estadiasHuespedes.csv";
    private int ultimoId; //Cuando se inicia la clase se carga el último ID usado

    private EstadiaDAO() {
        ultimoId = obtenerUltimoId() + 1;
    }

    public static EstadiaDAO getInstancia() {
        if (instancia == null) {
            instancia = new EstadiaDAO();
        }
        return instancia;
    }
    
    public int obtenerUltimoId() {
        int maxId = 0; 

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ESTADIAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue; 
                }

                try {
                    String[] datos = linea.split(",");
                    int idActual = Integer.parseInt(datos[0]);
                    
                    if (idActual > maxId) {
                        maxId = idActual; 
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Ignorando línea mal formada: " + linea);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Ignorando línea vacía o corrupta.");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("INFO: 'estadia.csv' no encontrado. Se asumirá ID 0.");
        } catch (IOException e) {
            System.err.println("Error crítico al leer el archivo de IDs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return maxId;
    }

    public boolean agregarEstadia(Estadia estadia) {
        String checkInFormateado = LocalDateFormat.format(estadia.getCheckIn());
        String checkOutFormateado = LocalDateFormat.format(estadia.getCheckOut());
        
        String nuevaLinea =  String.join(",",
            String.valueOf(ultimoId++),
            checkInFormateado,
            checkOutFormateado
        );
        
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RUTA_ESTADIAS, true)))) {
            out.println(nuevaLinea); 
            return true; 
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
    }   

    public boolean huespedEstadia(int idHuesped, int idEstadia) {
        String nuevaLinea =  String.join(",",
            Integer.toString(idHuesped),
            Integer.toString(idEstadia)
        );

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RUTA_HUESPEDES_ESTADIAS, true)))) {
            out.println(nuevaLinea); 
            return true; 
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean tieneEstadia(int idHuesped){
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_HUESPEDES_ESTADIAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue; 
                }

                try {
                    String[] datos = linea.split(",");
                    int idHuespedBD = Integer.parseInt(datos[1]);
                    
                    if(idHuespedBD == idHuesped) {
                        return true; 
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Ignorando línea mal formada: " + linea);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Ignorando línea vacía o corrupta.");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("INFO: 'estadiaHuespedes.csv' no encontrado. Se asumirá ID 0.");
        } catch (IOException e) {
            System.err.println("Error crítico al leer el archivo de IDs: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}