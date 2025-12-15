package tp_hotel.tp_hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean modificarHuesped(Huesped huesped, Boolean dniUnico) {
        if (huesped == null) {
            throw new IllegalArgumentException("Huésped no puede ser nulo.");
        }
        if(!documentoExistente(huesped.getTipoDocumento(), huesped.getNumeroDocumento())){
            throw new HuespedNoEncontradoException("El huesped no existe en el sistema.");
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

    public boolean darBajaHuesped(Integer id) {
        Optional<Huesped> huespedOptional = huespedRepository.findById(id);
        
        if(huespedOptional.isEmpty()){
            throw new HuespedNoEncontradoException("El id ingresado " + id + " no corresponde a ningún huésped.");
        }
       
        Huesped huesped = huespedOptional.get();
        if(huesped.getEstadiasComoTitular().size() > 0 || huesped.getEstadiasComoInvitado().size() > 0){
            throw new HuespedConEstadiaException("El huesped " + huesped.getApellido() + " " + huesped.getNombres() + ", ya se ha alojado anteriormente.");
        }
        
        huespedRepository.delete(huesped);

        return true;
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
