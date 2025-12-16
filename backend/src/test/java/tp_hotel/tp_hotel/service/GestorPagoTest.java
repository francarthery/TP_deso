package tp_hotel.tp_hotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import tp_hotel.tp_hotel.exceptions.FacturaPagadaException;
import tp_hotel.tp_hotel.exceptions.PagoInsuficienteException;
import tp_hotel.tp_hotel.exceptions.TipoPagoIncorrectoException;
import tp_hotel.tp_hotel.factory.TipoPagoFactory;
import tp_hotel.tp_hotel.model.EstadoFactura;
import tp_hotel.tp_hotel.model.PagoMoneda;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.MetodoPago;
import tp_hotel.tp_hotel.model.Pago;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.model.TipoPagoDTO;
import tp_hotel.tp_hotel.repository.PagoRepository;
import tp_hotel.tp_hotel.strategy.EstrategiaPago;
import tp_hotel.tp_hotel.strategy.StrategyFactory;

@ExtendWith(MockitoExtension.class)
public class GestorPagoTest {

    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private GestorFacturacion gestorFacturacion;

    @InjectMocks
    private GestorPago gestorPago;

    private Factura factura;
    private TipoPagoDTO tipoPagoDTO;
    private TipoPago tipoPago;

    @BeforeEach
    void setUp() {
        factura = new Factura();
        factura.setId(1);
        factura.setTotal(1000.0f);
        factura.setEstado(EstadoFactura.PENDIENTE);

        tipoPagoDTO = new TipoPagoDTO();
        tipoPagoDTO.setMetodoPago(MetodoPago.MONEDA);
        tipoPagoDTO.setImporte(1000.0f);

        tipoPago = new PagoMoneda();
        tipoPago.setMetodoPago(MetodoPago.MONEDA);
        tipoPago.setImporte(1000.0f);
    }

    @Test
    void ingresarPago_FacturaPagada_LanzaExcepcion() {
        try (MockedStatic<TipoPagoFactory> factoryMock = mockStatic(TipoPagoFactory.class);
             MockedStatic<StrategyFactory> strategyMock = mockStatic(StrategyFactory.class)) {
            
            factoryMock.when(() -> TipoPagoFactory.crearTipoPago(any())).thenReturn(tipoPago);
            
            EstrategiaPago strategy = mock(EstrategiaPago.class);
            when(strategy.validar(any())).thenReturn(true);
            strategyMock.when(() -> StrategyFactory.getStrategy(any())).thenReturn(strategy);

            factura.setEstado(EstadoFactura.PAGADA);
            when(gestorFacturacion.obtenerFacturaPorId(1)).thenReturn(factura);

            assertThrows(FacturaPagadaException.class, () -> {
                gestorPago.ingresarPago(Arrays.asList(tipoPagoDTO), 1);
            });
        }
    }

    @Test
    void ingresarPago_PagoInsuficiente_LanzaExcepcion() {
        try (MockedStatic<TipoPagoFactory> factoryMock = mockStatic(TipoPagoFactory.class);
             MockedStatic<StrategyFactory> strategyMock = mockStatic(StrategyFactory.class)) {
            
            tipoPago.setImporte(500.0f); // Insuficiente
            factoryMock.when(() -> TipoPagoFactory.crearTipoPago(any())).thenReturn(tipoPago);
            
            EstrategiaPago strategy = mock(EstrategiaPago.class);
            when(strategy.validar(any())).thenReturn(true);
            strategyMock.when(() -> StrategyFactory.getStrategy(any())).thenReturn(strategy);

            when(gestorFacturacion.obtenerFacturaPorId(1)).thenReturn(factura);

            assertThrows(PagoInsuficienteException.class, () -> {
                gestorPago.ingresarPago(Arrays.asList(tipoPagoDTO), 1);
            });
        }
    }

    @Test
    void ingresarPago_TipoPagoIncorrecto_LanzaExcepcion() {
        try (MockedStatic<TipoPagoFactory> factoryMock = mockStatic(TipoPagoFactory.class);
             MockedStatic<StrategyFactory> strategyMock = mockStatic(StrategyFactory.class)) {
            
            factoryMock.when(() -> TipoPagoFactory.crearTipoPago(any())).thenReturn(tipoPago);
            
            EstrategiaPago strategy = mock(EstrategiaPago.class);
            when(strategy.validar(any())).thenReturn(false); //Validación falla
            strategyMock.when(() -> StrategyFactory.getStrategy(any())).thenReturn(strategy);

            assertThrows(TipoPagoIncorrectoException.class, () -> {
                gestorPago.ingresarPago(Arrays.asList(tipoPagoDTO), 1);
            });
        }
    }

    @Test
    void ingresarPago_Exito() {
        try (MockedStatic<TipoPagoFactory> factoryMock = mockStatic(TipoPagoFactory.class);
             MockedStatic<StrategyFactory> strategyMock = mockStatic(StrategyFactory.class)) {
            
            factoryMock.when(() -> TipoPagoFactory.crearTipoPago(any())).thenReturn(tipoPago);
            
            EstrategiaPago strategy = mock(EstrategiaPago.class);
            when(strategy.validar(any())).thenReturn(true);
            strategyMock.when(() -> StrategyFactory.getStrategy(any())).thenReturn(strategy);

            when(gestorFacturacion.obtenerFacturaPorId(1)).thenReturn(factura);
            when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago());

            Pago resultado = gestorPago.ingresarPago(Arrays.asList(tipoPagoDTO), 1);

            assertNotNull(resultado);
            assertEquals(EstadoFactura.PAGADA, factura.getEstado());
            verify(pagoRepository).save(any(Pago.class));
        }
    }
}
