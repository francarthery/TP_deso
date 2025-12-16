package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tp_hotel.tp_hotel.exceptions.FechaDesdePosteriorHastaException;
import tp_hotel.tp_hotel.model.CategoriaHabitacion;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoHabitacion;
import tp_hotel.tp_hotel.model.EstadoReserva;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.HabitacionEstadoDTO;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
public class GestorHabitacionTest {

    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private EstadiaRepository estadiaRepository;

    @InjectMocks
    private GestorHabitacion gestorHabitacion;

    private Habitacion habitacion;
    private Reserva reserva;
    private Estadia estadia;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
        habitacion.setNumero("101");
        habitacion.setCategoria(CategoriaHabitacion.DOBLE_ESTANDAR);

        reserva = new Reserva();
        reserva.setId(1);
        reserva.setHabitacion(habitacion);
        reserva.setFechaInicio(LocalDate.now().plusDays(1));
        reserva.setFechaFin(LocalDate.now().plusDays(3));
        reserva.setEstado(EstadoReserva.PENDIENTE);

        estadia = new Estadia();
        estadia.setId(1);
        estadia.setHabitacion(habitacion);
        estadia.setCheckIn(LocalDate.now());
        estadia.setCheckOut(LocalDate.now().plusDays(1));
    }

    @Test
    void obtenerEstadoHabitaciones_FechasInvalidas_LanzaExcepcion() {
        assertThrows(FechaDesdePosteriorHastaException.class, () -> {
            gestorHabitacion.obtenerEstadoHabitaciones(LocalDate.now().plusDays(1), LocalDate.now());
        });
    }

    @Test
    void obtenerEstadoHabitaciones_Exito() {
        LocalDate desde = LocalDate.now();
        LocalDate hasta = LocalDate.now().plusDays(2);

        when(habitacionRepository.findAll()).thenReturn(Arrays.asList(habitacion));
        when(reservaRepository.findReservasPorFecha(desde, hasta)).thenReturn(Arrays.asList(reserva));
        when(estadiaRepository.findEstadiasPorFecha(desde, hasta)).thenReturn(Arrays.asList(estadia));

        List<HabitacionEstadoDTO> resultado = gestorHabitacion.obtenerEstadoHabitaciones(desde, hasta);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        HabitacionEstadoDTO dto = resultado.get(0);
        assertEquals("101", dto.getNumero());
        assertEquals(3, dto.getEstadosPorDia().size()); //Hoy, Mañana, Pasado mañana

        //Hoy: Ocupada (por estadia)
        assertEquals(EstadoHabitacion.OCUPADA, dto.getEstadosPorDia().get(0).getEstado());
        
        //Mañana: Ocupada
        assertEquals(EstadoHabitacion.OCUPADA, dto.getEstadosPorDia().get(1).getEstado());
        
        //Pasado mañana: Reservada (por reserva)
        assertEquals(EstadoHabitacion.RESERVADA, dto.getEstadosPorDia().get(2).getEstado());
    }
}
