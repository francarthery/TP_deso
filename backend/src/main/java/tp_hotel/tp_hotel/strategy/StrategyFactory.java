package tp_hotel.tp_hotel.strategy;

import java.util.HashMap;
import java.util.Map;

import tp_hotel.tp_hotel.model.MetodoPago;
import tp_hotel.tp_hotel.strategy.impl.*;

public class StrategyFactory {
    
    private static final Map<MetodoPago, EstrategiaPago> estrategias = new HashMap<>();

    static {
        estrategias.put(MetodoPago.MONEDA, new MonedaEstrategia());
        estrategias.put(MetodoPago.TARJETA_CREDITO, new TarjetaCreditoEstrategia());
        estrategias.put(MetodoPago.TARJETA_DEBITO, new TarjetaDebitoEstrategia());
        estrategias.put(MetodoPago.CHEQUE, new ChequeEstrategia());
    }

    public static EstrategiaPago getStrategy(MetodoPago metodo) {
        EstrategiaPago strategy = estrategias.get(metodo);
        if (strategy == null) {
            throw new IllegalArgumentException("No existe estrategia de validación para: " + metodo);
        }
        return strategy;
    }
}