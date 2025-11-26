package tp_hotel.tp_hotel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp_hotel.tp_hotel.model.EstadoReserva;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.model.ReservaDTO;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.HuespedRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@Service
public class GestorReserva {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final HuespedRepository huespedRepository;

    @Autowired
    public GestorReserva(ReservaRepository reservaRepository, HabitacionRepository habitacionRepository, HuespedRepository huespedRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
        this.huespedRepository = huespedRepository;
    }

    @Transactional
    public List<Reserva> crearReservas(List<ReservaDTO> reservasDTO) {
        List<Reserva> reservasCreadas = new ArrayList<>();

        for (ReservaDTO dto : reservasDTO) {
            if (dto.getNumeroHabitacion() == null || dto.getIdHuesped() == null) {
                throw new IllegalArgumentException("Faltan datos obligatorios (Habitación o Huésped).");
            }
            if (dto.getFechaInicio() == null || dto.getFechaFin() == null) {
                throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
            }
            if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
            }

            Habitacion habitacion = habitacionRepository.findById(dto.getNumeroHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + dto.getNumeroHabitacion()));
            
            Huesped huesped = huespedRepository.findById(dto.getIdHuesped())
                    .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado: " + dto.getIdHuesped()));

            Reserva reserva = new Reserva();
            reserva.setFechaInicio(dto.getFechaInicio());
            reserva.setFechaFin(dto.getFechaFin());
            reserva.setFechaCreacion(LocalDateTime.now());
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva.setHabitacion(habitacion);
            reserva.setTitular(huesped);

            reservasCreadas.add(reservaRepository.save(reserva));
        }

        return reservasCreadas;
    }
}
