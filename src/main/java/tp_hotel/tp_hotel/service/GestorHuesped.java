package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.List;

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
        if((apellido == null || apellido.isEmpty()) && (nombres == null || nombres.isEmpty()) && 
        (numeroDocumento == null || numeroDocumento.isEmpty()) && tipoDocumento == null){
            return huespedRepository.findAll();
        }
        String apellidoFiltro = (apellido != null && !apellido.isEmpty()) ? apellido : null;
        String nombresFiltro = (nombres != null && !nombres.isEmpty()) ? nombres : null;
        String numeroDocumentoFiltro = (numeroDocumento != null && !numeroDocumento.isEmpty()) ? numeroDocumento : null;

        return huespedRepository.buscarConFiltros(apellidoFiltro, nombresFiltro, tipoDocumento, numeroDocumentoFiltro);
    }

    public boolean documentoExistente(TipoDocumento tipoDocumento, String numeroDocumento){
        return huespedRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    public boolean modificarHuesped(Huesped huesped, Boolean dniUnico) {
         if (dniUnico && documentoExistente(huesped.getTipoDocumento(), huesped.getNumeroDocumento())) {
            List<Huesped> existente = huespedRepository.findByTipoDocumentoAndNumeroDocumento(
                huesped.getTipoDocumento(), 
                huesped.getNumeroDocumento()
            );

            if (existente.size() > 1 || existente.size() == 1 && !existente.get(0).getId().equals(huesped.getId())) {
                throw new IllegalArgumentException("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
            }
        }
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

    public Huesped buscarHuespedPorId(Integer id) {
        return huespedRepository.findById(id).orElse(null);
    }

    public Huesped registrarHuesped(Huesped huesped, Boolean dniUnico) {
        if (dniUnico && documentoExistente(huesped.getTipoDocumento(), huesped.getNumeroDocumento())) {
            throw new IllegalArgumentException("¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
        }
        return huespedRepository.save(huesped);
    }
}
