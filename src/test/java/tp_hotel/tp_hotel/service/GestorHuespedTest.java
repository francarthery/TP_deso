package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import tp_hotel.tp_hotel.exceptions.DocumentoExistenteException;
import tp_hotel.tp_hotel.exceptions.HuespedConEstadiaException;
import tp_hotel.tp_hotel.exceptions.HuespedNoEncontradoException;
import tp_hotel.tp_hotel.model.BusquedaHuespedDTO;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.TipoDocumento;
import tp_hotel.tp_hotel.repository.HuespedRepository;

@ExtendWith(MockitoExtension.class)
public class GestorHuespedTest {

    @Mock
    private HuespedRepository huespedRepository;

    @InjectMocks
    private GestorHuesped gestorHuesped;

    private Huesped huespedEjemplo;
    private BusquedaHuespedDTO busquedaDTO;

    @BeforeEach
    void setUp() {
        huespedEjemplo = new Huesped();
        huespedEjemplo.setId(1);
        huespedEjemplo.setNombres("Juan");
        huespedEjemplo.setApellido("Perez");
        huespedEjemplo.setTipoDocumento(TipoDocumento.DNI);
        huespedEjemplo.setNumeroDocumento("12345678");
        
        busquedaDTO = new BusquedaHuespedDTO();
    }


    @Test
    void buscarHuesped_SinFiltros_RetornaTodos() {

        when(huespedRepository.buscarConFiltros(null, null, null, null))
        .thenReturn(Arrays.asList(huespedEjemplo));

        List<Huesped> resultado = gestorHuesped.buscarHuesped(busquedaDTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(huespedRepository).buscarConFiltros(any(), any(), any(), any()); //Esto le pregunta al mock si recibio el llamado
        verify(huespedRepository, never()).findAll(); //Esto le pregunta al mock si NO recibio el llamado
    }

    @Test
    void buscarHuesped_ConFiltros_RetornaFiltrados() {
        busquedaDTO.setApellido("Perez");
        when(huespedRepository.buscarConFiltros("Perez", null, null, null))
            .thenReturn(Arrays.asList(huespedEjemplo));

        List<Huesped> resultado = gestorHuesped.buscarHuesped(busquedaDTO);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Perez", resultado.get(0).getApellido());
        verify(huespedRepository).buscarConFiltros("Perez", null, null, null);
    }

    @Test
    void buscarHuesped_SinResultados_LanzaExcepcion() {
        busquedaDTO.setApellido("Inexistente");
        when(huespedRepository.buscarConFiltros("Inexistente", null, null, null))
            .thenReturn(Collections.emptyList());

        assertThrows(HuespedNoEncontradoException.class, () -> {
            gestorHuesped.buscarHuesped(busquedaDTO);
        });
    }


    @Test
    void documentoExistente_Existe_RetornaTrue() {
        when(huespedRepository.existsByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, "12345678"))
            .thenReturn(true);

        assertTrue(gestorHuesped.documentoExistente(TipoDocumento.DNI, "12345678"));
    }

    @Test
    void documentoExistente_NoExiste_RetornaFalse() {
        when(huespedRepository.existsByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, "99999999"))
            .thenReturn(false);

        assertFalse(gestorHuesped.documentoExistente(TipoDocumento.DNI, "99999999"));
    }


    @Test
    void modificarHuesped_HuespedNulo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            gestorHuesped.modificarHuesped(null);
        });
    }

    @Test
    void modificarHuesped_NoExiste_LanzaExcepcion() {
        assertThrows(HuespedNoEncontradoException.class, () -> {
            gestorHuesped.modificarHuesped(huespedEjemplo);
        });
    }

    @Test
    void modificarHuesped_DniDuplicadoConOtroId_LanzaExcepcion() {
        // Simulamos que el DNI existe pero pertenece a OTRO ID (ej: ID 2)
        Huesped otroHuesped = new Huesped();
        otroHuesped.setId(2);
        
        when(huespedRepository.findById(any())).thenReturn(Optional.of(huespedEjemplo));
        when(huespedRepository.findByTipoDocumentoAndNumeroDocumento(any(), any())).thenReturn(Optional.of(otroHuesped));

        // El huespedEjemplo tiene ID 1
        assertThrows(DocumentoExistenteException.class, () -> {
            gestorHuesped.modificarHuesped(huespedEjemplo);
        });
    }

    @Test
    void modificarHuesped_Exito_RetornaTrue() {
        when(huespedRepository.findById(any())).thenReturn(Optional.of(huespedEjemplo));
        when(huespedRepository.findByTipoDocumentoAndNumeroDocumento(any(), any())).thenReturn(Optional.of(huespedEjemplo));
        when(huespedRepository.save(any(Huesped.class))).thenReturn(huespedEjemplo);

        boolean resultado = gestorHuesped.modificarHuesped(huespedEjemplo);

        assertTrue(resultado);
        verify(huespedRepository).save(huespedEjemplo);
    }

    @Test
    void modificarHuesped_FalloGuardado_LanzaExcepcion() {
        when(huespedRepository.findById(any())).thenReturn(Optional.of(huespedEjemplo));
        when(huespedRepository.findByTipoDocumentoAndNumeroDocumento(any(), any())).thenReturn(Optional.of(huespedEjemplo));
        when(huespedRepository.save(any(Huesped.class))).thenThrow(new RuntimeException("Error DB"));

        assertThrows(RuntimeException.class, () -> {
            gestorHuesped.modificarHuesped(huespedEjemplo);
        });
    }


    @Test
    void darBajaHuesped_IdNoExistente_LanzaExcepcion() {
        // Arrange
        Integer idInexistente = 99;
        when(huespedRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HuespedNoEncontradoException.class, () -> {
            gestorHuesped.darBajaHuesped(idInexistente);
        });
        
        // Verifica que NUNCA se llame al delete si no se encuentra
        verify(huespedRepository, never()).delete(any());
    }

    @Test
    void darBajaHuesped_ConEstadia_LanzaExcepcion() {
        // Arrange
        // Creamos un Mock del objeto Huesped para forzar que tieneEstadia devuelva true
        Huesped huespedConEstadia = mock(Huesped.class);
        when(huespedConEstadia.tieneEstadia()).thenReturn(true);
        // Opcional: Mockear nombres para que el mensaje de error no sea "null null"
        when(huespedConEstadia.getNombres()).thenReturn("Juan"); 
        when(huespedConEstadia.getApellido()).thenReturn("Perez");

        when(huespedRepository.findById(1)).thenReturn(Optional.of(huespedConEstadia));

        // Act & Assert
        assertThrows(HuespedConEstadiaException.class, () -> {
            gestorHuesped.darBajaHuesped(1);
        });

        // Verifica que no se borre si tiene estadía
        verify(huespedRepository, never()).delete(any());
    }

    @Test
    void darBajaHuesped_Exito_RetornaTrue() {
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huespedEjemplo));
        
        // Act
        boolean resultado = gestorHuesped.darBajaHuesped(1);

        // Assert
        assertTrue(resultado);
        verify(huespedRepository).delete(huespedEjemplo);
    }


    @Test
    void buscarHuespedesPorId_TodosEncontrados_RetornaLista() {
        List<Integer> ids = Arrays.asList(1);
        when(huespedRepository.findAllById(ids)).thenReturn(Arrays.asList(huespedEjemplo));

        List<Huesped> resultado = gestorHuesped.buscarHuespedesPorId(ids);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarHuespedesPorId_FaltanHuespedes_LanzaExcepcion() {
        List<Integer> ids = Arrays.asList(1, 2);
        // Solo encuentra uno
        when(huespedRepository.findAllById(ids)).thenReturn(Arrays.asList(huespedEjemplo));

        assertThrows(HuespedNoEncontradoException.class, () -> {
            gestorHuesped.buscarHuespedesPorId(ids);
        });
    }


    @Test
    void buscarHuespedPorId_Encontrado_RetornaHuesped() {
        // Arrange: El repo encuentra un huésped con ID 1
        when(huespedRepository.findById(1)).thenReturn(Optional.of(huespedEjemplo));

        // Act
        Huesped resultado = gestorHuesped.buscarHuespedPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Juan", resultado.getNombres());
    }

    @Test
    void buscarHuespedPorId_NoEncontrado_LanzaExcepcion() { // Sugiero cambiar el nombre del test también
        // Arrange
        when(huespedRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HuespedNoEncontradoException.class, () -> {
            gestorHuesped.buscarHuespedPorId(99);
        });
    }


    @Test
    void registrarHuesped_DniDuplicado_LanzaExcepcion() {
        when(huespedRepository.findByTipoDocumentoAndNumeroDocumento(any(), any())).thenReturn(Optional.of(new Huesped()));

        assertThrows(DocumentoExistenteException.class, () -> {
            gestorHuesped.registrarHuesped(huespedEjemplo);
        });
    }

    @Test
    void registrarHuesped_Exito_RetornaHuesped() {
        when(huespedRepository.findByTipoDocumentoAndNumeroDocumento(any(), any())).thenReturn(Optional.empty());
        when(huespedRepository.save(any(Huesped.class))).thenReturn(huespedEjemplo);

        Huesped resultado = gestorHuesped.registrarHuesped(huespedEjemplo);

        assertNotNull(resultado);
        verify(huespedRepository).save(huespedEjemplo);
    }


}
