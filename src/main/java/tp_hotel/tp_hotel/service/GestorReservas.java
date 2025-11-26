package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@Service
public class GestorReservas {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public GestorReservas(ReservaRepository reservaRepository, HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
    }

    public void crearReserva(Reserva r) {
    }

    public void confirmarReserva(Reserva r) {
    }

    public void cancelarReserva(int id) {
    }

    public List<Reserva> mostrarReservasPorHuesped(String apellido, String nombre) {
        return null;
    }

    public List<Habitacion> obtenerDisponibilidad(LocalDate desde, LocalDate hasta) {
        return null;
    }
}