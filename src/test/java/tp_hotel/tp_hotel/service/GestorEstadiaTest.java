package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tp_hotel.tp_hotel.exceptions.EstadiaNoExistenteException;
import tp_hotel.tp_hotel.exceptions.HabitacionNoExistenteException;
import tp_hotel.tp_hotel.model.Consumo;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadiaDTO;
import tp_hotel.tp_hotel.model.Habitacion;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.HabitacionRepository;
import tp_hotel.tp_hotel.repository.HuespedRepository;
import tp_hotel.tp_hotel.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
public class GestorEstadiaTest {

    @Mock
    private EstadiaRepository estadiaRepository;
    @Mock
    private HuespedRepository huespedRepository;
    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private GestorEstadia gestorEstadia;

    private EstadiaDTO estadiaDTO;
    private Habitacion habitacion;
    private Huesped huesped;
    private Estadia estadia;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
        habitacion.setNumero("101");
        habitacion.setCostoNoche(100.0f);

        huesped = new Huesped();
        huesped.setId(1);
        huesped.setEstadiasComoTitular(new ArrayList<>());
        huesped.setEstadiasComoInvitado(new ArrayList<>());

        estadiaDTO = new EstadiaDTO();
        estadiaDTO.setNumeroHabitacion("101");
        estadiaDTO.setCheckIn(LocalDate.now());
        estadiaDTO.setCheckOut(LocalDate.now().plusDays(1));
        estadiaDTO.setIdHuespedTitular(1);
        estadiaDTO.setIdsHuespedesInvitados(new ArrayList<>());

        estadia = new Estadia();
        estadia.setId(1);
        estadia.setHabitacion(habitacion);
        estadia.setConsumos(new ArrayList<>());
    }


    @Test
    void crearEstadias_CheckInPosteriorCheckOut_LanzaExcepcion() {
        estadiaDTO.setCheckIn(LocalDate.now().plusDays(2));
        estadiaDTO.setCheckOut(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            gestorEstadia.crearEstadias(Arrays.asList(estadiaDTO));
        });
    }

    @Test
    void crearEstadias_CheckInNoHoy_LanzaExcepcion() {
        estadiaDTO.setCheckIn(LocalDate.now().plusDays(1));
        estadiaDTO.setCheckOut(LocalDate.now().plusDays(2));

        assertThrows(IllegalArgumentException.class, () -> {
            gestorEstadia.crearEstadias(Arrays.asList(estadiaDTO));
        });
    }

    @Test
    void crearEstadias_HabitacionNoExiste_LanzaExcepcion() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            gestorEstadia.crearEstadias(Arrays.asList(estadiaDTO));
        });
    }

    @Test
    void crearEstadias_HuespedNoExiste_LanzaExcepcion() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacion));
        when(huespedRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            gestorEstadia.crearEstadias(Arrays.asList(estadiaDTO));
        });
    }

    @Test
    void crearEstadias_HabitacionOcupada_LanzaExcepcion() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacion));
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(estadiaRepository.findByNumeroYRango(any(), any(), any())).thenReturn(new Estadia());

        assertThrows(IllegalArgumentException.class, () -> {
            gestorEstadia.crearEstadias(Arrays.asList(estadiaDTO));
        });
    }

    @Test
    void crearEstadias_Exito() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacion));
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(estadiaRepository.findByNumeroYRango(any(), any(), any())).thenReturn(null);
        when(estadiaRepository.save(any(Estadia.class))).thenReturn(estadia);

        List<Estadia> resultado = gestorEstadia.crearEstadias(Arrays.asList(estadiaDTO));

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(estadiaRepository).save(any(Estadia.class));
    }


    @Test
    void tieneEstadia_True() {
        huesped.getEstadiasComoTitular().add(estadia);
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));

        assertTrue(gestorEstadia.tieneEstadia(1));
    }

    @Test
    void tieneEstadia_False() {
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        assertFalse(gestorEstadia.tieneEstadia(1));
    }


    @Test
    void buscarEstadiaActiva_HabitacionNoExiste_LanzaExcepcion() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.empty());
        assertThrows(HabitacionNoExistenteException.class, () -> gestorEstadia.buscarEstadiaActiva("101"));
    }

    @Test
    void buscarEstadiaActiva_NoHayActiva_LanzaExcepcion() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacion));
        when(estadiaRepository.findActivaByHabitacionNumero(any(), any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> gestorEstadia.buscarEstadiaActiva("101"));
    }

    @Test
    void buscarEstadiaActiva_Exito() {
        when(habitacionRepository.findById("101")).thenReturn(Optional.of(habitacion));
        when(estadiaRepository.findActivaByHabitacionNumero(any(), any())).thenReturn(Optional.of(estadia));

        Estadia resultado = gestorEstadia.buscarEstadiaActiva("101");
        assertNotNull(resultado);
    }


    @Test
    void buscarEstadias_RetornaLista() {
        when(estadiaRepository.findByHabitacionNumero("101")).thenReturn(Arrays.asList(estadia));
        List<Estadia> resultado = gestorEstadia.buscarEstadias("101");
        assertFalse(resultado.isEmpty());
    }


    @Test
    void obtenerConsumosDeEstadia_NoExiste_LanzaExcepcion() {
        when(estadiaRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EstadiaNoExistenteException.class, () -> gestorEstadia.obtenerConsumosDeEstadia(1));
    }

    @Test
    void obtenerConsumosDeEstadia_Exito() {
        when(estadiaRepository.findById(1)).thenReturn(Optional.of(estadia));
        List<Consumo> resultado = gestorEstadia.obtenerConsumosDeEstadia(1);
        assertNotNull(resultado);
    }
}
