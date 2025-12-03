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
    private final ReservaRepository reservaRepository;

    @Autowired
    public GestorEstadia(EstadiaRepository estadiaRepository, HuespedRepository huespedRepository, 
                         HabitacionRepository habitacionRepository, ReservaRepository reservaRepository) {
       this.estadiaRepository = estadiaRepository;
       this.huespedRepository = huespedRepository;
       this.habitacionRepository = habitacionRepository;
       this.reservaRepository = reservaRepository;
    }

    @Transactional
    public List<Estadia> crearEstadias(List<EstadiaDTO> estadiasDTO) {
        List<Estadia> estadiasCreadas = new ArrayList<>();

        for (EstadiaDTO dto : estadiasDTO) {
            if (dto.getNumeroHabitacion() == null || dto.getIdHuesped() == null) {
                throw new IllegalArgumentException("Faltan datos obligatorios (Habitación o Huésped).");
            }
            if (dto.getCheckIn() == null || dto.getCheckOut() == null) {
                throw new IllegalArgumentException("Las fechas de Check-In y Check-Out son obligatorias.");
            }
            if (dto.getCheckIn().isAfter(dto.getCheckOut())) {
                throw new IllegalArgumentException("La fecha de Check-In no puede ser posterior al Check-Out.");
            }

            Habitacion habitacion = habitacionRepository.findById(dto.getNumeroHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + dto.getNumeroHabitacion()));
            
            Huesped huesped = huespedRepository.findById(dto.getIdHuesped())
                    .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado: " + dto.getIdHuesped()));

            Reserva reserva = null;
            if (dto.getIdReserva() != null) {
                reserva = reservaRepository.findById(dto.getIdReserva())
                        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + dto.getIdReserva()));
                
                if (!reserva.getHabitacion().getNumero().equals(habitacion.getNumero())) {
                     throw new IllegalArgumentException("La reserva no coincide con la habitación seleccionada.");
                }
            }

            boolean ocupada = estadiaRepository.findAll().stream().anyMatch(e -> 
                e.getHabitacion().getNumero().equals(habitacion.getNumero()) &&
                !dto.getCheckIn().isAfter(e.getCheckOut()) && 
                !dto.getCheckOut().isBefore(e.getCheckIn())
            );

            boolean reservada = reservaRepository.findAll().stream().anyMatch(r -> 
                r.getHabitacion().getNumero().equals(habitacion.getNumero()) &&
                r.getId().equals(dto.getIdReserva()) &&
                !dto.getCheckIn().isAfter(r.getFechaFin()) && 
                !dto.getCheckOut().isBefore(r.getFechaInicio())
            );

            if (reservada) {
                throw new IllegalArgumentException("La habitación " + habitacion.getNumero() + " está reservada en las fechas seleccionadas.");
            }

            if (ocupada) {
                throw new IllegalArgumentException("La habitación " + habitacion.getNumero() + " ya está ocupada en las fechas seleccionadas.");
            }

            Estadia estadia = new Estadia();
            estadia.setCheckIn(dto.getCheckIn());
            estadia.setCheckOut(dto.getCheckOut());
            estadia.setHabitacion(habitacion);
            estadia.setHuesped(huesped);
            estadia.setReserva(reserva);

            estadiasCreadas.add(estadiaRepository.save(estadia));
        }
        return estadiasCreadas;
    }

    public boolean asociarHuespedAEstadia(long estadiaID, Huesped titular, List<Huesped> acompaniantes){
        Estadia estadia = estadiaRepository.findById(estadiaID).orElse(null);
        if (estadia == null) {
            System.out.println("Estadía no encontrada");
            return false;
        }
        
        if (titular != null) {
            titular.agregarEstadia(estadia);
            huespedRepository.save(titular);
        } else {
            System.out.println("No ha sido posible cargar la estadía al titular");
            return false;
        }

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
    
    public void iniciarEstadia(Estadia e) {
    }

    public void finalizarEstadia(Integer id) {
    }
}