package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

import tp_hotel.tp_hotel.exceptions.CuitYaExistenteException;
import tp_hotel.tp_hotel.exceptions.FacturaAsociadaException;
import tp_hotel.tp_hotel.exceptions.HuespedNoEncontradoException;
import tp_hotel.tp_hotel.exceptions.PersonaJuridicaNoExistenteException;
import tp_hotel.tp_hotel.exceptions.ResponsablePagoNoExistenteException;
import tp_hotel.tp_hotel.model.BusquedaResponsablePagoDTO;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.model.ResponsablePago;
import tp_hotel.tp_hotel.repository.HuespedRepository;
import tp_hotel.tp_hotel.repository.PersonaFisicaRepository;
import tp_hotel.tp_hotel.repository.PersonaJuridicaRepository;
import tp_hotel.tp_hotel.repository.ResponsablePagoRepository;

@ExtendWith(MockitoExtension.class)
public class GestorResponsablePagoTest {

    @Mock
    private HuespedRepository huespedRepository;
    @Mock
    private PersonaJuridicaRepository personaJuridicaRepository;
    @Mock
    private PersonaFisicaRepository personaFisicaRepository;
    @Mock
    private ResponsablePagoRepository responsablePagoRepository;

    @InjectMocks
    private GestorResponsablePago gestorResponsablePago;

    private PersonaJuridica personaJuridica;
    private PersonaFisica personaFisica;
    private Huesped huesped;
    private BusquedaResponsablePagoDTO busquedaDTO;

    @BeforeEach
    void setUp() {
        huesped = new Huesped();
        huesped.setId(1);
        huesped.setApellido("Perez");
        huesped.setNombres("Juan");

        personaJuridica = new PersonaJuridica();
        personaJuridica.setId(1);
        personaJuridica.setRazonSocial("Empresa SA");
        personaJuridica.setCuit("30-12345678-9");
        personaJuridica.setFacturas(new ArrayList<>());

        personaFisica = new PersonaFisica();
        personaFisica.setId(2);
        personaFisica.setHuesped(huesped);
        personaFisica.setRazonSocial("Perez Juan");
        personaFisica.setFacturas(new ArrayList<>());

        busquedaDTO = new BusquedaResponsablePagoDTO();
    }

    // --- Tests para cuitUnico ---

    @Test
    void cuitUnico_NoExiste_RetornaTrue() {
        when(personaJuridicaRepository.findByCuit("30-12345678-9")).thenReturn(Optional.empty());
        assertTrue(gestorResponsablePago.cuitUnico("30-12345678-9"));
    }

    @Test
    void cuitUnico_Existe_RetornaFalse() {
        when(personaJuridicaRepository.findByCuit("30-12345678-9")).thenReturn(Optional.of(personaJuridica));
        assertFalse(gestorResponsablePago.cuitUnico("30-12345678-9"));
    }

    // --- Tests para buscarPersonaJuridica ---

    @Test
    void buscarPersonaJuridica_Encontrado_RetornaLista() {
        when(personaJuridicaRepository.findByCuitYRazonSocial(any(), any()))
            .thenReturn(Arrays.asList(personaJuridica));

        List<PersonaJuridica> resultado = gestorResponsablePago.buscarPersonaJuridica(busquedaDTO);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void buscarPersonaJuridica_NoEncontrado_LanzaExcepcion() {
        when(personaJuridicaRepository.findByCuitYRazonSocial(any(), any()))
            .thenReturn(Collections.emptyList());

        assertThrows(PersonaJuridicaNoExistenteException.class, () -> {
            gestorResponsablePago.buscarPersonaJuridica(busquedaDTO);
        });
    }


    @Test
    void darAltaPersonaFisica_YaExiste_RetornaExistente() {
        when(personaFisicaRepository.findByHuespedId(1)).thenReturn(Optional.of(personaFisica));
        
        ResponsablePago resultado = gestorResponsablePago.darAltaPersonaFisica(1);
        
        assertEquals(personaFisica, resultado);
        verify(personaFisicaRepository, never()).save(any());
    }

    @Test
    void darAltaPersonaFisica_HuespedNoExiste_LanzaExcepcion() {
        when(personaFisicaRepository.findByHuespedId(1)).thenReturn(Optional.empty());
        when(huespedRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(HuespedNoEncontradoException.class, () -> {
            gestorResponsablePago.darAltaPersonaFisica(1);
        });
    }

    @Test
    void darAltaPersonaFisica_Nuevo_CreaYGuarda() {
        when(personaFisicaRepository.findByHuespedId(1)).thenReturn(Optional.empty());
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huesped));
        when(personaFisicaRepository.save(any(PersonaFisica.class))).thenAnswer(i -> i.getArgument(0));

        ResponsablePago resultado = gestorResponsablePago.darAltaPersonaFisica(1);

        assertNotNull(resultado);
        assertEquals("Perez Juan", resultado.getRazonSocial());
        verify(personaFisicaRepository).save(any(PersonaFisica.class));
    }


    @Test
    void darAltaPersonaJuridica_CuitDuplicado_LanzaExcepcion() {
        when(personaJuridicaRepository.findByCuit(personaJuridica.getCUIT())).thenReturn(Optional.of(new PersonaJuridica()));

        assertThrows(CuitYaExistenteException.class, () -> {
            gestorResponsablePago.darAltaPersonaJuridica(personaJuridica);
        });
    }

    @Test
    void darAltaPersonaJuridica_Exito_Guarda() {
        when(personaJuridicaRepository.findByCuit(personaJuridica.getCUIT())).thenReturn(Optional.empty());
        when(personaJuridicaRepository.save(personaJuridica)).thenReturn(personaJuridica);

        ResponsablePago resultado = gestorResponsablePago.darAltaPersonaJuridica(personaJuridica);

        assertEquals(personaJuridica, resultado);
        verify(personaJuridicaRepository).save(personaJuridica);
    }


    @Test
    void buscarResponsablePago_Encontrados_RetornaListaCombinada() {
        when(personaJuridicaRepository.findByCuitYRazonSocial(any(), any())).thenReturn(Arrays.asList(personaJuridica));
        when(personaFisicaRepository.findByCuitYRazonSocial(any(), any())).thenReturn(Arrays.asList(personaFisica));

        List<ResponsablePago> resultado = gestorResponsablePago.buscarResponsablePago(busquedaDTO);

        assertEquals(2, resultado.size());
    }

    @Test
    void buscarResponsablePago_NoEncontrados_LanzaExcepcion() {
        when(personaJuridicaRepository.findByCuitYRazonSocial(any(), any())).thenReturn(Collections.emptyList());
        when(personaFisicaRepository.findByCuitYRazonSocial(any(), any())).thenReturn(Collections.emptyList());

        assertThrows(ResponsablePagoNoExistenteException.class, () -> {
            gestorResponsablePago.buscarResponsablePago(busquedaDTO);
        });
    }


    @Test
    void darBajaResponsable_NoExiste_LanzaExcepcion() {
        when(responsablePagoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponsablePagoNoExistenteException.class, () -> {
            gestorResponsablePago.darBajaResponsable(99);
        });
    }

    @Test
    void darBajaResponsable_ConFacturas_LanzaExcepcion() {
        personaJuridica.getFacturas().add(new Factura());
        when(responsablePagoRepository.findById(1)).thenReturn(Optional.of(personaJuridica));

        assertThrows(FacturaAsociadaException.class, () -> {
            gestorResponsablePago.darBajaResponsable(1);
        });
        verify(responsablePagoRepository, never()).delete(any());
    }

    @Test
    void darBajaResponsable_SinFacturas_Elimina() {
        when(responsablePagoRepository.findById(1)).thenReturn(Optional.of(personaJuridica));

        gestorResponsablePago.darBajaResponsable(1);

        verify(responsablePagoRepository).delete(personaJuridica);
    }
}
