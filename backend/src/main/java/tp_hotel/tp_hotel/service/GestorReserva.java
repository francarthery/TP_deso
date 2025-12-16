package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp_hotel.tp_hotel.model.EstadoReserva;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.model.ReservaDTO;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@Service
public class GestorReserva {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public GestorReserva(ReservaRepository reservaRepository, HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Transactional
    public List<Reserva> crearReservas(List<ReservaDTO> reservasDTO) {
        List<Reserva> reservasCreadas = new ArrayList<>();

        for (ReservaDTO dto : reservasDTO) {
            if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
            }

            Habitacion habitacion = habitacionRepository.findById(dto.getNumeroHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + dto.getNumeroHabitacion()));

            Reserva reserva = new Reserva();
            reserva.setFechaInicio(dto.getFechaInicio());
            reserva.setFechaFin(dto.getFechaFin());
            reserva.setFechaCreacion(LocalDateTime.now());
            reserva.setEstado(EstadoReserva.PENDIENTE);
            reserva.setHabitacion(habitacion);
            reserva.setNombreHuesped(dto.getNombreHuesped());
            reserva.setApellidoHuesped(dto.getApellidoHuesped());
            reserva.setTelefonoHuesped(dto.getTelefonoHuesped());

            reservasCreadas.add(reservaRepository.save(reserva));
        }

        return reservasCreadas;
    }
    
    public void confirmarReserva(Reserva r) {
    }

    public void cancelarReserva(Integer id) {
        reservaRepository.deleteById(id);
    }

    @Transactional
    public void cancelarReservasPorHuesped(List<Integer> idsReservas){
        reservaRepository.cancelarReservas(idsReservas, EstadoReserva.CANCELADA);
    }

    public List<Reserva> mostrarReservasNoCanceladasPorHuesped(String apellido, String nombre) {
        String apellidoFiltro = (apellido != null && !apellido.isEmpty()) ? apellido : null;
        String nombresFiltro = (nombre != null && !nombre.isEmpty()) ? nombre : null;
        return reservaRepository.findByApellidoHuespedNombreHuesped(apellidoFiltro, nombresFiltro, EstadoReserva.CANCELADA);
    }
    
    public List<Reserva> buscarReservasSolapadas(String numeroHabitacion, LocalDate checkIn, LocalDate checkOut) {
        return reservaRepository.findReservasSolapadas(
            numeroHabitacion, 
            checkIn, 
            checkOut
        );
    }

    public List<Habitacion> obtenerDisponibilidad(LocalDate desde, LocalDate hasta) {
        return null;
    }
}
