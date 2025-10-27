package service;

import repository.HuespedDAO;
import domain.Huesped;
import domain.IVA;
import domain.Direccion;
import java.time.LocalDate;
import java.util.List;
import domain.TipoDocumento;
import exceptions.HuespedNoEncontradoException;
import java.util.stream.Stream;
import java.util.stream.Collectors;


public class GestorHuesped {
    
    private final HuespedDAO huespedDAO;
    
    public GestorHuesped(HuespedDAO huespedDAO) {
       this.huespedDAO = huespedDAO;
    }

    public List<Huesped> buscarHuesped(String apellido, String nombres, TipoDocumento tipoDocumento, String numeroDocumento) {
        List<Huesped> huespedes;

        try {
            huespedes = huespedDAO.obtenerTodos();
        } catch (HuespedNoEncontradoException e) {
            System.out.println("Error al buscar huesped: " + e.getMessage());
            e.printStackTrace();
            return List.of(); 
        }

        Stream<Huesped> stream = huespedes.stream();
        if (apellido != null && !apellido.isEmpty()) {
            stream = stream.filter(h -> h.getApellido().equalsIgnoreCase(apellido));
        }

        if (nombres != null && !nombres.isEmpty()) {
            stream = stream.filter(h -> h.getNombres().equalsIgnoreCase(nombres));
        }

        if (tipoDocumento != null) {
            stream = stream.filter(h -> h.getTipoDocumento() == tipoDocumento);
        }

        if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
            stream = stream.filter(h -> h.getNumeroDocumento().equalsIgnoreCase(numeroDocumento));
        }

        return stream.collect(Collectors.toList());
    }

    public boolean documentoExistente(String tipoDocumento, String numeroDocumento){
        return huespedDAO.documentoExistente(tipoDocumento, numeroDocumento);
    }

    public Huesped darAltaHuesped(String apellido, String nombres, TipoDocumento documento, String numeroDocumento,
                                String cuit, IVA posicionFrenteAlIVA, LocalDate fechaDeNacimiento, String telefono, 
                                String email, String ocupacion, String nacionalidad, String pais, String provincia,
                                String ciudad, String calle, String numero, String piso, String departamento, 
                                String codigoPostal) {
        
        Direccion direccion = new Direccion.Builder()
                .pais(pais)
                .provincia(provincia)
                .localidad(ciudad)
                .calle(calle)
                .numero(numero)
                .piso(piso)
                .departamento(departamento)
                .codigoPostal(codigoPostal)
                .build();         
        
        Huesped huesped = new Huesped.Builder()
                .id(0)
                .apellido(apellido)
                .nombres(nombres)
                .tipoDocumento(documento)
                .numeroDocumento(numeroDocumento)
                .cuit(cuit)
                .posicionFrenteAlIVA(posicionFrenteAlIVA)
                .fechaDeNacimiento(fechaDeNacimiento)
                .telefono(telefono)
                .email(email)
                .ocupacion(ocupacion)
                .nacionalidad(nacionalidad)
                .direccion(direccion)
                .build();
        
        if(huespedDAO.agregarHuesped(huesped)) return huesped;
        else return null;
    }

    public boolean modificarHuesped(Huesped huesped) {
        return huespedDAO.modificarHuesped(huesped);
    }

    public boolean darBajaHuesped(Huesped huesped) {
        return huespedDAO.darBajaHuesped(huesped);
    }
}
