package tp_hotel.tp_hotel.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadiaDTO;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.HuespedRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@Service
public class GestorEstadia {

    private final EstadiaRepository estadiaRepository;
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public GestorEstadia(EstadiaRepository estadiaRepository, HuespedRepository huespedRepository, 
                         HabitacionRepository habitacionRepository, ReservaRepository reservaRepository) {
       this.estadiaRepository = estadiaRepository;
       this.huespedRepository = huespedRepository;
       this.habitacionRepository = habitacionRepository;
    }

    @Transactional
    public List<Estadia> crearEstadias(List<EstadiaDTO> estadiasDTO) {
        List<Estadia> estadiasCreadas = new ArrayList<>();

        for (EstadiaDTO dto : estadiasDTO) {
            if (dto.getNumeroHabitacion() == null) {
                throw new IllegalArgumentException("Faltan datos obligatorios (Habitación).");
            }
            if (dto.getCheckIn() == null || dto.getCheckOut() == null) {
                throw new IllegalArgumentException("Las fechas de Check-In y Check-Out son obligatorias.");
            }
            if (dto.getCheckIn().isAfter(dto.getCheckOut())) {
                throw new IllegalArgumentException("La fecha de Check-In no puede ser posterior al Check-Out.");
            }

            Habitacion habitacion = habitacionRepository.findById(dto.getNumeroHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + dto.getNumeroHabitacion()));
            
            Integer idTitular = dto.getIdHuespedTitular();
            if (idTitular == null) {
                 throw new IllegalArgumentException("El ID del titular es obligatorio.");
            }
          
            Huesped titular = huespedRepository.findById(idTitular)
                    .orElseThrow(() -> new IllegalArgumentException("Huésped titular no encontrado: " + idTitular));

            boolean ocupada = estadiaRepository.findAll().stream().anyMatch(e -> 
                e.getHabitacion().getNumero().equals(habitacion.getNumero()) &&
                !dto.getCheckIn().isAfter(e.getCheckOut()) && 
                !dto.getCheckOut().isBefore(e.getCheckIn())
            );

            if (ocupada) {
                throw new IllegalArgumentException("La habitación " + habitacion.getNumero() + " ya está ocupada en las fechas seleccionadas.");
            }

            Estadia estadia = new Estadia();
            estadia.setCheckIn(dto.getCheckIn());
            estadia.setCheckOut(dto.getCheckOut());
            estadia.setHabitacion(habitacion);
            
            estadia.setHuespedTitular(titular);
            titular.agregarEstadiaComoTitular(estadia);

            if (dto.getIdsHuespedesInvitados() != null) {
                List<Huesped> invitados = new ArrayList<>();
                for (Integer idInvitado : dto.getIdsHuespedesInvitados()) {
                    Huesped invitado = huespedRepository.findById(idInvitado)
                        .orElseThrow(() -> new IllegalArgumentException("Huésped invitado no encontrado: " + idInvitado));
                    
                    invitados.add(invitado);
                    invitado.agregarEstadiaComoInvitado(estadia);
                }
                estadia.setHuespedInvitados(invitados);
            }

            estadiasCreadas.add(estadiaRepository.save(estadia));
            
        }
        return estadiasCreadas;
    }
    

    public boolean tieneEstadia(int huespedID){
        return huespedRepository.findById(huespedID)
                .map(h -> !h.getEstadiasComoTitular().isEmpty() || !h.getEstadiasComoInvitado().isEmpty())
                .orElse(false);
    }
    
    public void iniciarEstadia(Estadia e) {
    }

    public void finalizarEstadia(Integer id) {
    }
}