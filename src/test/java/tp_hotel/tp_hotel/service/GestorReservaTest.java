package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tp_hotel.tp_hotel.model.EstadoReserva;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.Reserva;
import tp_hotel.tp_hotel.model.ReservaDTO;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
public class GestorReservaTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private GestorReserva gestorReserva;

    private ReservaDTO reservaDTO;
    private Habitacion habitacion;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
        habitacion.setNumero("101");
        
        reservaDTO = new ReservaDTO();
        reservaDTO.setFechaInicio(LocalDate.now().plusDays(1));
        reservaDTO.setFechaFin(LocalDate.now().plusDays(5));
        reservaDTO.setNumeroHabitacion("101");
        reservaDTO.setNombreHuesped("Juan");
        reservaDTO.setApellidoHuesped("Perez");
        reservaDTO.setTelefonoHuesped("123456789");

        reserva = new Reserva();
        reserva.setId(1);
        reserva.setFechaInicio(reservaDTO.getFechaInicio());
        reserva.setFechaFin(reservaDTO.getFechaFin());
        reserva.setHabitacion(habitacion);
        reserva.setEstado(EstadoReserva.PENDIENTE);
    }


    @Test
    void crearReservas_Exito_RetornaListaReservas() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacion));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        List<Reserva> resultado = gestorReserva.crearReservas(Arrays.asList(reservaDTO));

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(EstadoReserva.PENDIENTE, resultado.get(0).getEstado());
        verify(habitacionRepository).findById("101");
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReservas_FechaInicioPosteriorFin_LanzaExcepcion() {
        reservaDTO.setFechaInicio(LocalDate.now().plusDays(6));
        reservaDTO.setFechaFin(LocalDate.now().plusDays(5));

        assertThrows(IllegalArgumentException.class, () -> {
            gestorReserva.crearReservas(Arrays.asList(reservaDTO));
        });
        
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReservas_HabitacionNoEncontrada_LanzaExcepcion() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            gestorReserva.crearReservas(Arrays.asList(reservaDTO));
        });

        verify(reservaRepository, never()).save(any());
    }


    @Test
    void cancelarReserva_LlamaDeleteById() {
        gestorReserva.cancelarReserva(1);
        verify(reservaRepository).deleteById(1);
    }


    @Test
    void cancelarReservasPorHuesped_LlamaCancelarReservas() {
        List<Integer> ids = Arrays.asList(1, 2, 3);
        gestorReserva.cancelarReservasPorHuesped(ids);
        verify(reservaRepository).cancelarReservas(ids, EstadoReserva.CANCELADA);
    }


    @Test
    void mostrarReservasNoCanceladasPorHuesped_RetornaLista() {
        when(reservaRepository.findByApellidoHuespedNombreHuesped("Perez", "Juan", EstadoReserva.CANCELADA))
            .thenReturn(Arrays.asList(reserva));

        List<Reserva> resultado = gestorReserva.mostrarReservasNoCanceladasPorHuesped("Perez", "Juan");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void mostrarReservasNoCanceladasPorHuesped_FiltrosNulos_LlamaConNulos() {
        when(reservaRepository.findByApellidoHuespedNombreHuesped(null, null, EstadoReserva.CANCELADA))
            .thenReturn(Collections.emptyList());

        gestorReserva.mostrarReservasNoCanceladasPorHuesped("", null);

        verify(reservaRepository).findByApellidoHuespedNombreHuesped(null, null, EstadoReserva.CANCELADA);
    }


    @Test
    void buscarReservasSolapadas_RetornaLista() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);
        when(reservaRepository.findReservasSolapadas("101", checkIn, checkOut))
            .thenReturn(Arrays.asList(reserva));

        List<Reserva> resultado = gestorReserva.buscarReservasSolapadas("101", checkIn, checkOut);

        assertEquals(1, resultado.size());
        verify(reservaRepository).findReservasSolapadas("101", checkIn, checkOut);
    }

    
    @Test
    void obtenerDisponibilidad_RetornaNull() {
        assertNull(gestorReserva.obtenerDisponibilidad(LocalDate.now(), LocalDate.now()));
    }
}
