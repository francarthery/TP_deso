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

import tp_hotel.tp_hotel.exceptions.ConsumoNoExistenteException;
import tp_hotel.tp_hotel.exceptions.EstadiaNoExistenteException;
import tp_hotel.tp_hotel.exceptions.FacturasNoExistentesException;
import tp_hotel.tp_hotel.exceptions.ResponsablePagoNoExistenteException;
import tp_hotel.tp_hotel.model.BusquedaFacturaResponsableDTO;
import tp_hotel.tp_hotel.model.Consumo;
import tp_hotel.tp_hotel.model.DatosFacturaDTO;
import tp_hotel.tp_hotel.model.Direccion;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.Huesped;
import tp_hotel.tp_hotel.model.PersonaFisica;
import tp_hotel.tp_hotel.model.TipoDocumento;
import tp_hotel.tp_hotel.model.TipoFactura;
import tp_hotel.tp_hotel.repository.ConsumoRepository;
import tp_hotel.tp_hotel.repository.EstadiaRepository;
import tp_hotel.tp_hotel.repository.FacturaRepository;
import tp_hotel.tp_hotel.repository.ResponsablePagoRepository;

@ExtendWith(MockitoExtension.class)
public class GestorFacturacionTest {

    @Mock
    private FacturaRepository facturaRepository;
    @Mock
    private ConsumoRepository consumoRepository;
    @Mock
    private ResponsablePagoRepository responsablePagoRepository;
    @Mock
    private EstadiaRepository estadiaRepository;

    @InjectMocks
    private GestorFacturacion gestorFacturacion;

    private Factura factura;
    private DatosFacturaDTO datosFacturaDTO;
    private Consumo consumo;
    private Estadia estadia;
    private PersonaFisica responsablePago;

    @BeforeEach
    void setUp() {
        // Setup básico de objetos
        responsablePago = new PersonaFisica();
        responsablePago.setId(1);
        responsablePago.setFacturas(new ArrayList<>());
        Huesped h = new Huesped();
        h.setApellido("Perez");
        h.setNombres("Juan");
        h.setCuit("20-12345678-9");
        Direccion dir = new Direccion();
        dir.setCalle("Calle Falsa");
        dir.setNumero("123");
        h.setDireccion(dir);
        responsablePago.setHuesped(h);

        factura = new Factura();
        factura.setId(1);
        factura.setNumero("0000000001");
        factura.setTipo(TipoFactura.B);
        factura.setFecha(LocalDate.now());
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setResponsablePago(responsablePago);

        consumo = new Consumo();
        consumo.setId(1);
        consumo.setDescripcion("Coca Cola");
        consumo.setCantidad(2);
        consumo.setMonto(150.0f);

        estadia = new Estadia();
        estadia.setId(1);

        datosFacturaDTO = new DatosFacturaDTO();
        datosFacturaDTO.setIdConsumos(Arrays.asList(1));
        datosFacturaDTO.setIdEstadia(1);
        datosFacturaDTO.setIdResponsablePago(1);
        datosFacturaDTO.setFecha(LocalDate.now());
    }

    @Test
    void obtenerFacturaPorId_Existe_RetornaFactura() {
        when(facturaRepository.findById(1)).thenReturn(Optional.of(factura));
        Factura resultado = gestorFacturacion.obtenerFacturaPorId(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
    }

    @Test
    void obtenerFacturaPorId_NoExiste_LanzaExcepcion() {
        when(facturaRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(FacturasNoExistentesException.class, () -> gestorFacturacion.obtenerFacturaPorId(99));
    }


    @Test
    void obtenerFacturasPorHabitacionNoPagas_Exito() {
        when(facturaRepository.findByNumeroHabitacionYEstado("101", EstadoFactura.PENDIENTE))
            .thenReturn(Arrays.asList(factura));
        
        List<Factura> resultado = gestorFacturacion.obtenerFacturasPorHabitacionNoPagas("101");
        assertFalse(resultado.isEmpty());
    }

    @Test
    void obtenerFacturasPorHabitacionNoPagas_Vacio_LanzaExcepcion() {
        when(facturaRepository.findByNumeroHabitacionYEstado("101", EstadoFactura.PENDIENTE))
            .thenReturn(Collections.emptyList());
        
        assertThrows(FacturasNoExistentesException.class, () -> gestorFacturacion.obtenerFacturasPorHabitacionNoPagas("101"));
    }


    @Test
    void obtenerFacturasNoPagasPorResponsable_ValidacionCampos_LanzaExcepcion() {
        BusquedaFacturaResponsableDTO dto = new BusquedaFacturaResponsableDTO();
        dto.setTipoDocumento(TipoDocumento.DNI);
        // Falta numeroDocumento
        assertThrows(IllegalArgumentException.class, () -> gestorFacturacion.obtenerFacturasNoPagasPorResponsable(dto));
    }

    @Test
    void obtenerFacturasNoPagasPorResponsable_SinCriterios_LanzaExcepcion() {
        BusquedaFacturaResponsableDTO dto = new BusquedaFacturaResponsableDTO();
        assertThrows(IllegalArgumentException.class, () -> gestorFacturacion.obtenerFacturasNoPagasPorResponsable(dto));
    }

    @Test
    void obtenerFacturasNoPagasPorResponsable_Exito() {
        BusquedaFacturaResponsableDTO dto = new BusquedaFacturaResponsableDTO();
        dto.setCuit("20-12345678-9");
        
        when(facturaRepository.buscarFacturasPorResponsable("20-12345678-9", null, null, EstadoFactura.PENDIENTE))
            .thenReturn(Arrays.asList(factura));

        List<Factura> resultado = gestorFacturacion.obtenerFacturasNoPagasPorResponsable(dto);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void obtenerFacturasNoPagasPorResponsable_NoEncontrado_LanzaExcepcion() {
        BusquedaFacturaResponsableDTO dto = new BusquedaFacturaResponsableDTO();
        dto.setCuit("20-12345678-9");
        
        when(facturaRepository.buscarFacturasPorResponsable(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());

        assertThrows(FacturasNoExistentesException.class, () -> gestorFacturacion.obtenerFacturasNoPagasPorResponsable(dto));
    }


    @Test
    void existeFacturaDeResponsable_True() {
        when(facturaRepository.findByResponsableId(1)).thenReturn(Arrays.asList(factura));
        assertTrue(gestorFacturacion.existeFacturaDeResponsable(1));
    }

    @Test
    void existeFacturaDeResponsable_False() {
        when(facturaRepository.findByResponsableId(1)).thenReturn(Collections.emptyList());
        assertFalse(gestorFacturacion.existeFacturaDeResponsable(1));
    }


    @Test
    void generarFactura_ConsumoNoExiste_LanzaExcepcion() {
        when(consumoRepository.findAllById(any())).thenReturn(Collections.emptyList());
        
        assertThrows(ConsumoNoExistenteException.class, () -> gestorFacturacion.generarFactura(datosFacturaDTO));
    }

    @Test
    void generarFactura_EstadiaNoExiste_LanzaExcepcion() {
        when(consumoRepository.findAllById(any())).thenReturn(Arrays.asList(consumo));
        when(estadiaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EstadiaNoExistenteException.class, () -> gestorFacturacion.generarFactura(datosFacturaDTO));
    }

    @Test
    void generarFactura_ResponsableNoExiste_LanzaExcepcion() {
        when(consumoRepository.findAllById(any())).thenReturn(Arrays.asList(consumo));
        when(estadiaRepository.findById(1)).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResponsablePagoNoExistenteException.class, () -> gestorFacturacion.generarFactura(datosFacturaDTO));
    }

    @Test
    void generarFactura_Exito() {
        when(consumoRepository.findAllById(any())).thenReturn(Arrays.asList(consumo));
        when(estadiaRepository.findById(1)).thenReturn(Optional.of(estadia));
        when(responsablePagoRepository.findById(1)).thenReturn(Optional.of(responsablePago));
        when(facturaRepository.findUltimoNumeroPorTipo(any())).thenReturn("0000000000");
        when(facturaRepository.save(any(Factura.class))).thenAnswer(i -> {
            Factura f = i.getArgument(0);
            f.setId(10);
            return f;
        });

        Integer idFactura = gestorFacturacion.generarFactura(datosFacturaDTO);
        
        assertNotNull(idFactura);
        assertEquals(10, idFactura);
        verify(facturaRepository).save(any(Factura.class));
    }


    @Test
    void generarPDFFactura_Exito() {
        when(facturaRepository.findById(1)).thenReturn(Optional.of(factura));
        
        byte[] pdf = gestorFacturacion.generarPDFFactura(1);
        
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
