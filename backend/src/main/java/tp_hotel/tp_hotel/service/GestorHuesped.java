package tp_hotel.tp_hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp_hotel.tp_hotel.exceptions.DocumentoExistenteException;
import tp_hotel.tp_hotel.exceptions.HuespedConEstadiaException;
import tp_hotel.tp_hotel.exceptions.HuespedNoEncontradoException;
import tp_hotel.tp_hotel.model.BusquedaHuespedDTO;
import tp_hotel.tp_hotel.model.Huesped;
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
        
        List<Huesped> huespedes = huespedRepository.buscarConFiltros(apellido, nombres, tipoDocumento, numeroDocumento);    
        if(huespedes.isEmpty()) {
            throw new HuespedNoEncontradoException("¡CUIDADO! No se encontraron huéspedes con los criterios de búsqueda proporcionados.");
        }
        return huespedes;
    }

    public boolean documentoExistente(TipoDocumento tipoDocumento, String numeroDocumento){
        return huespedRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    public boolean modificarHuesped(Huesped huesped) {
        if (huesped == null || huesped.getId() == null) {
            throw new IllegalArgumentException("Huésped y el id no pueden ser nulos.");
        }

        Optional<Huesped> huespedExistente = huespedRepository.findById(huesped.getId());
        if (huespedExistente.isEmpty()) {
            throw new HuespedNoEncontradoException("El huésped con ID " + huesped.getId() + " no existe.");
        }

        Optional<Huesped> existente = huespedRepository.findByTipoDocumentoAndNumeroDocumento(
            huesped.getTipoDocumento(), 
            huesped.getNumeroDocumento()
        );

        if (existente.isPresent() && huesped.getId() != null && !existente.get().getId().equals(huesped.getId())) {
            throw new DocumentoExistenteException("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
        }

        huespedRepository.save(huesped);
        return true;
    }

    public boolean darBajaHuesped(Integer id) {
        Optional<Huesped> huespedOptional = huespedRepository.findById(id);
        
        if(huespedOptional.isEmpty()){
            throw new HuespedNoEncontradoException("El id ingresado " + id + " no corresponde a ningún huésped.");
        }
       
        Huesped huesped = huespedOptional.get();
        if(huesped.tieneEstadia()){
            throw new HuespedConEstadiaException("El huesped " + huesped.getApellido() + " " + huesped.getNombres() + ", ya se ha alojado anteriormente.");
        }
        
        huespedRepository.delete(huesped);

        return true;
    }

    public Huesped buscarHuespedPorId(Integer id) {
        return huespedRepository.findById(id).orElseThrow(() -> new HuespedNoEncontradoException("El id " + id + " no corresponde a ningún huésped."));
    }

    public List<Huesped> buscarHuespedesPorId(List<Integer> ids) {
        List<Huesped> huespedes = huespedRepository.findAllById(ids);
        if(huespedes.size() != ids.size()) {
            throw new HuespedNoEncontradoException("¡CUIDADO! Algunos huéspedes no fueron encontrados.");
        }
        return huespedes; 
    }

    @Transactional
    public Huesped registrarHuesped(Huesped huesped) {
        Optional<Huesped> existente = huespedRepository.findByTipoDocumentoAndNumeroDocumento(
            huesped.getTipoDocumento(), 
            huesped.getNumeroDocumento()
        );


        if (existente.isPresent()) {
            throw new DocumentoExistenteException("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
        }

        huesped.setId(null);
        return huespedRepository.save(huesped);
    }
}
