package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp_hotel.tp_hotel.exceptions.EstadiaNoExistenteException;
import tp_hotel.tp_hotel.exceptions.HabitacionNoExistenteException;
import tp_hotel.tp_hotel.model.Consumo;
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
            if (dto.getCheckIn().isAfter(dto.getCheckOut())) {
                throw new IllegalArgumentException("La fecha de Check-In no puede ser posterior al Check-Out.");
            }
            if (!dto.getCheckIn().isEqual(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de Check-In no puede ser distinta al dia de hoy.");
            }

            Habitacion habitacion = habitacionRepository.findById(dto.getNumeroHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + dto.getNumeroHabitacion()));
            
            Integer idTitular = dto.getIdHuespedTitular();
          
            Huesped titular = huespedRepository.findById(idTitular)
                    .orElseThrow(() -> new IllegalArgumentException("Huésped titular no encontrado: " + idTitular));


            Estadia ocupada = estadiaRepository.findByNumeroYRango(dto.getNumeroHabitacion(), dto.getCheckIn(), dto.getCheckOut());

            if (ocupada != null) {
                throw new IllegalArgumentException("La habitación " + habitacion.getNumero() + " ya está ocupada en las fechas seleccionadas.");
            }

            Estadia estadia = new Estadia();
            estadia.setCheckIn(dto.getCheckIn());
            estadia.setCheckOut(dto.getCheckOut());
            estadia.setHabitacion(habitacion);
            
            estadia.setHuespedTitular(titular);
            titular.agregarEstadiaComoTitular(estadia);
            
            Consumo consumo = new Consumo();
            Float totalHabitacion = habitacion.getCostoNoche() * 
                                     (dto.getCheckOut().toEpochDay() - dto.getCheckIn().toEpochDay());
            consumo.setMonto(totalHabitacion);
            consumo.setCantidad(1);
            consumo.setDescripcion("Alojamiento - Número habitación: " + habitacion.getNumero() + ".");
            consumo.setFecha(dto.getCheckIn());
            consumo.setEstadia(estadia);
            estadia.getConsumos().add(consumo);


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
    
    public Estadia buscarEstadiaActiva(String numeroHabitacion) {
        if(habitacionRepository.findById(numeroHabitacion).isEmpty()) {
            throw new HabitacionNoExistenteException("La habitación con número " + numeroHabitacion + " no existe.");
        }
        return estadiaRepository.findActivaByHabitacionNumero(numeroHabitacion, LocalDate.now())
        .orElseThrow(() -> new IllegalArgumentException("No hay estadía activa para la habitación: " + numeroHabitacion));
    }
    
    public List<Estadia> buscarEstadias(String numeroHabitacion){
        return estadiaRepository.findByHabitacionNumero(numeroHabitacion);
    }
    
    public List<Consumo> obtenerConsumosDeEstadia(Integer idEstadia){
        Optional<Estadia> estadia = estadiaRepository.findById(idEstadia);
    
        if (!estadia.isPresent()) {
            throw new EstadiaNoExistenteException("La estadía con ID " + idEstadia + " no existe.");
        }

        return estadia.get().getConsumos();
    }

    public void finalizarEstadia(Integer id) {
    }
}
