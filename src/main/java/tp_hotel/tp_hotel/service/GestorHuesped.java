package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp_hotel.tp_hotel.model.Direccion;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.IVA;
import tp_hotel.tp_hotel.model.TipoDocumento;
import tp_hotel.tp_hotel.repository.HuespedRepository;

@Service
public class GestorHuesped {
    
    private final HuespedRepository huespedRepository;
    
    @Autowired
    public GestorHuesped(HuespedRepository huespedRepository) {
       this.huespedRepository = huespedRepository;
    }

    public List<Huesped> buscarHuesped(String apellido, String nombres, TipoDocumento tipoDocumento, String numeroDocumento) {
        List<Huesped> huespedes = huespedRepository.findAll();

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

    public boolean documentoExistente(TipoDocumento tipoDocumento, String numeroDocumento){
        return huespedRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    public Huesped darAltaHuesped(String apellido, String nombres, TipoDocumento documento, String numeroDocumento,
                                String cuit, IVA posicionFrenteAlIVA, LocalDate fechaDeNacimiento, String telefono, 
                                String email, String ocupacion, String nacionalidad, String pais, String provincia,
                                String ciudad, String calle, String numero, String piso, String departamento, 
                                String codigoPostal) {
        
        Direccion direccion = Direccion.builder()
                .pais(pais)
                .provincia(provincia)
                .localidad(ciudad)
                .calle(calle)
                .numero(numero)
                .piso(piso)
                .departamento(departamento)
                .codigoPostal(codigoPostal)
                .build();         
        
        Huesped huesped = Huesped.builder()
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
        
        if(huesped == null){
            return null;
        }
        return huespedRepository.save(huesped);
    }

    public boolean modificarHuesped(Huesped huesped) {
        if (huesped == null) {
            return false;
        }
        try {
            huespedRepository.save(huesped);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean darBajaHuesped(Huesped huesped) {
        if (huesped == null) {
            return false;
        }
        try {
            huespedRepository.delete(huesped);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Huesped buscarHuespedPorId(int id) {
        return huespedRepository.findById(id).orElse(null);
    }
}
