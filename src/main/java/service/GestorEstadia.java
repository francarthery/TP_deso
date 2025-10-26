package service;
import java.util.List;

import domain.Huesped;
import repository.EstadiaDAO;
import repository.HuespedDAO;

public class GestorEstadia{

    private static GestorEstadia instancia;
    private EstadiaDAO estadiaDAO = EstadiaDAO.getInstancia();


    private GestorEstadia() {}

    public static GestorEstadia getInstancia() {
        if (instancia == null) {
            instancia = new GestorEstadia();
        }
        return instancia;
    }

    public boolean asociarHuespedAEstadia(int estadiaID, Huesped titular, List<Huesped> acompaniantes){
        if(estadiaDAO.huespedEstadia(estadiaID, titular.getId())) titular.agregarEstadia(estadiaID);
        else{
            System.out.println("No ha sido posible cargar la estadía al titular");
            return false;
        }

        for (Huesped huesped : acompaniantes) {
            if(estadiaDAO.huespedEstadia(estadiaID, huesped.getId())) huesped.agregarEstadia(estadiaID);
            else{
                System.out.println("No ha sido posible cargar la estadía al huesped " + huesped.getNombres() + " " + huesped.getApellido() + ".");
                return false;
            }
        }
        return true;
    }

    public boolean tieneEstadia(int huespedID){
        return estadiaDAO.tieneEstadia(huespedID);
    }
}