package tp_hotel.tp_hotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp_hotel.tp_hotel.model.CategoriaHabitacion;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoDiaDTO;
import tp_hotel.tp_hotel.model.EstadoHabitacion;
import tp_hotel.tp_hotel.model.EstadoReserva;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.HabitacionEstadoDTO;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@Service
public class GestorHabitacion {

    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;
    private final EstadiaRepository estadiaRepository;

    @Autowired
    public GestorHabitacion(HabitacionRepository habitacionRepository, ReservaRepository reservaRepository, EstadiaRepository estadiaRepository) {
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
        this.estadiaRepository = estadiaRepository;
    }

    public List<HabitacionEstadoDTO> obtenerEstadoHabitaciones(LocalDate desde, LocalDate hasta) {
        List<Habitacion> habitaciones = habitacionRepository.findAll();
        List<Reserva> reservas = reservaRepository.findReservasPorFecha(desde, hasta); 
        List<Estadia> estadias = estadiaRepository.findEstadiasPorFecha(desde, hasta); 

        List<HabitacionEstadoDTO> reporte = new ArrayList<>();

        for (Habitacion habitacion : habitaciones) {
            HabitacionEstadoDTO dto = new HabitacionEstadoDTO();
            dto.setNumero(habitacion.getNumero());
            dto.setCategoria(habitacion.getCategoria());
            
            List<EstadoDiaDTO> estadosDia = new ArrayList<>();
            
            for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
                EstadoDiaDTO estadoDia = new EstadoDiaDTO();
                estadoDia.setFecha(fecha);
                
                LocalDate finalFecha = fecha;
                
                Estadia estadiaActiva = estadias.stream()
                    .filter(e -> e.getHabitacion().getNumero().equals(habitacion.getNumero()) &&
                            !finalFecha.isBefore(e.getCheckIn()) && finalFecha.isBefore(e.getCheckOut()))
                    .findFirst()
                    .orElse(null);

                if (estadiaActiva != null) {
                    estadoDia.setEstado(EstadoHabitacion.OCUPADA);
                    estadoDia.setIdReferencia(estadiaActiva.getId().intValue());
                } else {
                    Reserva reservaActiva = reservas.stream()
                        .filter(r -> r.getHabitacion().getNumero().equals(habitacion.getNumero()) &&
                                !finalFecha.isBefore(r.getFechaInicio()) && finalFecha.isBefore(r.getFechaFin()) &&
                                !r.getEstado().equals(EstadoReserva.CANCELADA))
                        .findFirst()
                        .orElse(null);
                    
                    if (reservaActiva != null) {
                        estadoDia.setEstado(EstadoHabitacion.RESERVADA);
                        estadoDia.setIdReferencia(reservaActiva.getId());
                    } else {
                        estadoDia.setEstado(EstadoHabitacion.DISPONIBLE);
                    }
                }
                estadosDia.add(estadoDia);
            }
            dto.setEstadosPorDia(estadosDia);
            reporte.add(dto);
        }
        return reporte;
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