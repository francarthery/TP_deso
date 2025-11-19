package tp_hotel.tp_hotel.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.HuespedRepository;

@Service
public class GestorEstadia {

    private final EstadiaRepository estadiaRepository;
    private final HuespedRepository huespedRepository;

    @Autowired
    public GestorEstadia(EstadiaRepository estadiaRepository, HuespedRepository huespedRepository) {
       this.estadiaRepository = estadiaRepository;
       this.huespedRepository = huespedRepository;
    }

    public boolean asociarHuespedAEstadia(long estadiaID, Huesped titular, List<Huesped> acompaniantes){
        Estadia estadia = estadiaRepository.findById(estadiaID).orElse(null);
        if (estadia == null) {
            System.out.println("Estadía no encontrada");
            return false;
        }

        // Asociar al titular
        if (titular != null) {
            titular.agregarEstadia(estadia);
            huespedRepository.save(titular);
        } else {
            System.out.println("No ha sido posible cargar la estadía al titular");
            return false;
        }

        // Asociar a los acompañantes
        for (Huesped huesped : acompaniantes) {
            if (huesped != null) {
                huesped.agregarEstadia(estadia);
                huespedRepository.save(huesped);
            } else {
                System.out.println("No ha sido posible cargar la estadía a un acompañante.");
                return false;
            }
        }
        return true;
    }

    public boolean tieneEstadia(int huespedID){
        return huespedRepository.findById(huespedID)
                .map(h -> !h.getEstadias().isEmpty())
                .orElse(false);
    }
}