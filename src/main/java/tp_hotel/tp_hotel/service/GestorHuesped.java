package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp_hotel.tp_hotel.exceptions.HuespedNoEncontradoException;
import tp_hotel.tp_hotel.model.BusquedaHuespedDTO;
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

    public List<Huesped> buscarHuesped(BusquedaHuespedDTO busquedaHuespedDTO) {
        String apellido = busquedaHuespedDTO.getApellido();
        String nombres = busquedaHuespedDTO.getNombres();
        TipoDocumento tipoDocumento = busquedaHuespedDTO.getTipoDocumento();
        String numeroDocumento = busquedaHuespedDTO.getNumeroDocumento();
        
        if(apellido == null && nombres == null && numeroDocumento == null && tipoDocumento == null) return huespedRepository.findAll();
        
        List<Huesped> huespedes = huespedRepository.buscarConFiltros(apellido, nombres, tipoDocumento, numeroDocumento);    
        if(huespedes.isEmpty()) {
            throw new HuespedNoEncontradoException("¡CUIDADO! No se encontraron huéspedes con los criterios de búsqueda proporcionados.");
        }
        return huespedes;
    }

    public boolean documentoExistente(TipoDocumento tipoDocumento, String numeroDocumento){
        return huespedRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    public boolean modificarHuesped(Huesped huesped, Boolean dniUnico) {
        if (huesped == null) {
            throw new IllegalArgumentException("Huésped no puede ser nulo.");
        }
        if(!documentoExistente(huesped.getTipoDocumento(), huesped.getNumeroDocumento())){
            throw new IllegalArgumentException("El huesped no existe en el sistema.");
        }

        Huesped existente = huespedRepository.findByTipoDocumentoAndNumeroDocumento(
            huesped.getTipoDocumento(), 
            huesped.getNumeroDocumento()
        );

        if (dniUnico && !existente.getId().equals(huesped.getId())) {
            throw new IllegalArgumentException("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
        }

        huesped.setId(existente.getId());

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

    public Huesped buscarHuespedPorId(Integer id) {
        return huespedRepository.findById(id).orElse(null);
    }

    public List<Huesped> buscarHuespedesPorId(List<Integer> ids) {
        List<Huesped> huespedes = huespedRepository.findAllById(ids);
        if(huespedes.size() != ids.size()) {
            throw new IllegalArgumentException("¡CUIDADO! Algunos huéspedes no fueron encontrados.");
        }
        return huespedes; 
    }

    public Huesped registrarHuesped(Huesped huesped, Boolean dniUnico) {
        if (dniUnico && documentoExistente(huesped.getTipoDocumento(), huesped.getNumeroDocumento())) {
            throw new IllegalArgumentException("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
        }
        if(!dniUnico){
            Huesped existente = huespedRepository.findByTipoDocumentoAndNumeroDocumento(
                huesped.getTipoDocumento(), 
                huesped.getNumeroDocumento()
            );
            huesped.setId(existente.getId());
        }

        return huespedRepository.save(huesped);
    }
}
