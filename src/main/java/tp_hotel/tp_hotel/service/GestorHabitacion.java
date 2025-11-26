package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.CategoriaHabitacion;
import tp_hotel.tp_hotel.model.EstadoHabitacion;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.repository.HabitacionRepository;

@Service
public class GestorHabitacion {

    private final HabitacionRepository habitacionRepository;

    @Autowired
    public GestorHabitacion(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    public Map<Habitacion, EstadoHabitacion> obtenerEstadoHabitaciones(LocalDate desde, LocalDate hasta) {
        return null;
    }

    public void cambiarEstadoHabitacion(int num, EstadoHabitacion estado, LocalDate desde, LocalDate hasta) {
    }

    public List<Habitacion> buscarHabitacionesDisponibles(CategoriaHabitacion categoria, LocalDate desde, LocalDate hasta) {
        return null;
    }

    public Habitacion getHabitacionPorNumero(int num) {
        return null;
    }
}