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
import java.io.File;

import domain.Direccion;
import domain.Huesped;
import domain.IVA;
import domain.TipoDocumento;
import exceptions.HuespedNoEncontradoException;
import java.io.FileNotFoundException;



public class HuespedDAO {
    
    private static HuespedDAO instancia;
    private static final DateTimeFormatter LocalDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String RUTA_ARCHIVO = "src/main/resources/data/huespedes.csv";
    private int ultimoId; //Cuando se inicia la clase se carga el último ID usado
    private HuespedDAO() {
        ultimoId = obtenerUltimoId() + 1;
    }

    public static HuespedDAO getInstancia() {
        if (instancia == null) {
            instancia = new HuespedDAO();
        }
        return instancia;
    }

    public int obtenerUltimoId() {
        int maxId = 0; 

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
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
            System.out.println("INFO: 'huespedes.csv' no encontrado. Se asumirá ID 0.");
        } catch (IOException e) {
            System.err.println("Error crítico al leer el archivo de IDs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return maxId;
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
                    LocalDate fechaDeNacimiento = LocalDate.parse(datos[7], LocalDateFormat);
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
                        .id(id)
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

    public String convertirHuespedAString(Huesped huesped, String fechaFormateada) {
        Direccion direccion = huesped.getDireccion();
        return String.join(",",
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
            direccion.getPais(),
            direccion.getProvincia(),
            direccion.getLocalidad(),
            direccion.getCalle(),
            direccion.getNumero(),
            direccion.getPiso(),
            direccion.getDepartamento(),
            direccion.getCodigoPostal()
        );
    }

    public boolean agregarHuesped(Huesped huesped) {
        String fechaFormateada = LocalDateFormat.format(huesped.getFechaDeNacimiento());
        huesped.setId(ultimoId++);
        String nuevaLinea = convertirHuespedAString(huesped, fechaFormateada);
        
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
    
    public boolean modificarHuesped(Huesped huespedModificado) {
        File archivoOriginal = new File(RUTA_ARCHIVO);
        File archivoTemporal = new File(RUTA_ARCHIVO + ".tmp");

        boolean modificado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoOriginal));
             PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(archivoTemporal)))) {
            
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    String[] datos = linea.split(",");
                    int idActual = Integer.parseInt(datos[0]);
                    
                    if (idActual == huespedModificado.getId()) {
                        String fechaFormateada = LocalDateFormat.format(huespedModificado.getFechaDeNacimiento());
                        String lineaModificada = convertirHuespedAString(huespedModificado, fechaFormateada);
                        pw.println(lineaModificada);
                        modificado = true;
                    } else {
                        pw.println(linea);
                    }
                } catch (Exception e) {
                    pw.println(linea);
                }
            }

        } catch (IOException e) {
            System.err.println("Error al modificar huésped: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        if (!modificado) {
            archivoTemporal.delete();
            System.err.println("No se encontró un huésped con ID " + huespedModificado.getId() + " para modificar.");
            return false;
        }

        if (!archivoOriginal.delete()) {
            System.err.println("Error: No se pudo borrar el archivo original.");
            return false;
        }

        if (!archivoTemporal.renameTo(archivoOriginal)) {
            System.err.println("Error: No se pudo renombrar el archivo temporal.");
            return false;
        }

        return true;
    }
}
