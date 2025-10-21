package repository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import domain.Rol;
import domain.UsuarioDTO;

public class UsuarioDAO {
    
    private static UsuarioDAO instance;

    private UsuarioDAO() {}
    
    public static UsuarioDAO getInstancia() {
        if (instance == null) {
            instance = new UsuarioDAO();
        }
        return instance;
    }

    private String rutaRecurso = "data/usuarios.csv"; 

    public UsuarioDTO encontrarPorUsername(String username) {
        
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(rutaRecurso); //Se trae a memoria el archivo como stream de bytes

        if (inputStream == null) {
            System.err.println("Error: No se pudo encontrar el archivo " + rutaRecurso);
            return null;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                
                if (datos.length == 4) { 
                    int idFile = Integer.parseInt(datos[0]);
                    String usernameFile = datos[1];
                    String passwordFile = datos[2];
                    Rol rolFile = Rol.valueOf(datos[3]); //Convertir String a enum Rol
                    
                    if (usernameFile.equals(username)) {
                        return new UsuarioDTO(idFile, usernameFile, passwordFile, rolFile);
                    }
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo DAO: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
