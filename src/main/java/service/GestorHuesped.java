package service;

import repository.HuespedDAO;
import domain.HuespedDTO;
import java.util.List;
import domain.TipoDocumento;
import exceptions.HuespedNoEncontradoException;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class GestorHuesped {
    
    private static GestorHuesped instancia;
    private HuespedDAO huespedDAO = HuespedDAO.getInstancia();

    private GestorHuesped() {}

    public static GestorHuesped getInstancia() {
        if (instancia == null) {
            instancia = new GestorHuesped();
        }
        return instancia;
    }

    public List<HuespedDTO> buscarHuesped(String apellido, String nombres, TipoDocumento tipoDocumento, String numeroDocumento) {
        List<HuespedDTO> huespedes;

        try {
            huespedes = huespedDAO.obtenerTodos();
        } catch (HuespedNoEncontradoException e) {
            System.out.println("Error al buscar huesped: " + e.getMessage());
            e.printStackTrace();
            return List.of(); 
        }

        Stream<HuespedDTO> stream = huespedes.stream();
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
}
