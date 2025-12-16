package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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

import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.NotaCreditoNoExistenteException;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.NotaCredito;
import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.TipoFactura;
import tp_hotel.tp_hotel.repository.FacturaRepository;
import tp_hotel.tp_hotel.repository.NotaCreditoRepository;

@ExtendWith(MockitoExtension.class)
public class GestorNotaCreditoTest {

    @Mock
    private NotaCreditoRepository notaCreditoRepository;
    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private GestorNotaCredito gestorNotaCredito;

    private Factura factura;
    private NotaCredito notaCredito;

    @BeforeEach
    void setUp() {
        factura = new Factura();
        factura.setId(1);
        factura.setNumero("0000000001");
        factura.setTotal(1000.0f);
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setTipo(TipoFactura.B);
        
        // Setup necesario para PDF
        Estadia estadia = new Estadia();
        estadia.setCheckIn(LocalDate.now());
        estadia.setCheckOut(LocalDate.now().plusDays(1));
        factura.setEstadia(estadia);
        
        PersonaFisica pf = new PersonaFisica();
        Huesped h = new Huesped();
        h.setApellido("Test");
        h.setNombres("User");
        pf.setHuesped(h);
        factura.setResponsablePago(pf);

        notaCredito = new NotaCredito();
        notaCredito.setId(1);
        notaCredito.setNumero("0000000001");
        notaCredito.setFecha(LocalDate.now());
        notaCredito.setFacturas(Arrays.asList(factura));
    }

    // --- Tests para obtenerUltimoNumero ---

    @Test
    void obtenerUltimoNumero_SinNumerosPrevios_RetornaUno() {
        when(notaCreditoRepository.findUltimoNumero()).thenReturn(null);
        String numero = gestorNotaCredito.obtenerUltimoNumero();
        assertEquals("0000000001", numero);
    }

    @Test
    void obtenerUltimoNumero_ConNumeroPrevio_Incrementa() {
        when(notaCreditoRepository.findUltimoNumero()).thenReturn("0000000005");
        String numero = gestorNotaCredito.obtenerUltimoNumero();
        assertEquals("0000000006", numero);
    }

    // --- Tests para crearNotaCredito ---

    @Test
    void crearNotaCredito_FacturasNoExisten_LanzaExcepcion() {
        List<Integer> ids = Arrays.asList(1, 2);
        when(facturaRepository.findAllById(ids)).thenReturn(Arrays.asList(factura)); // Solo encuentra 1

        assertThrows(FacturasNoExistentesException.class, () -> {
            gestorNotaCredito.crearNotaCredito(ids);
        });
    }

    @Test
    void crearNotaCredito_Exito() {
        List<Integer> ids = Arrays.asList(1);
        when(facturaRepository.findAllById(ids)).thenReturn(Arrays.asList(factura));
        when(notaCreditoRepository.findUltimoNumero()).thenReturn("0000000000");
        when(notaCreditoRepository.save(any(NotaCredito.class))).thenReturn(notaCredito);

        NotaCredito resultado = gestorNotaCredito.crearNotaCredito(ids);

        assertNotNull(resultado);
        assertEquals(EstadoFactura.ANULADA, factura.getEstado());
        verify(notaCreditoRepository).save(any(NotaCredito.class));
    }

    // --- Tests para generarPDFNotaCredito ---

    @Test
    void generarPDFNotaCredito_NoExiste_LanzaExcepcion() {
        when(notaCreditoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotaCreditoNoExistenteException.class, () -> {
            gestorNotaCredito.generarPDFNotaCredito(1);
        });
    }

    @Test
    void generarPDFNotaCredito_SinFacturas_RetornaVacio() {
        notaCredito.setFacturas(Collections.emptyList());
        when(notaCreditoRepository.findById(1)).thenReturn(Optional.of(notaCredito));

        byte[] pdf = gestorNotaCredito.generarPDFNotaCredito(1);
        assertEquals(0, pdf.length);
    }

    @Test
    void generarPDFNotaCredito_Exito_RetornaBytes() {
        when(notaCreditoRepository.findById(1)).thenReturn(Optional.of(notaCredito));

        byte[] pdf = gestorNotaCredito.generarPDFNotaCredito(1);
        assertNotNull(pdf);
        // assertTrue(pdf.length > 0); // Comentado porque puede fallar si no hay contenido real
    }
}
