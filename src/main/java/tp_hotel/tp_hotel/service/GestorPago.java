package tp_hotel.tp_hotel.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.TipoPago;
import tp_hotel.tp_hotel.repository.FacturaRepository;
import tp_hotel.tp_hotel.repository.TipoPagoRepository;

@Service
public class GestorPago{

    private final FacturaRepository facturaRepository;
    private final TipoPagoRepository tipoPagoRepository;

    @Autowired
    public GestorPago(FacturaRepository facturaRepository, TipoPagoRepository tipoPagoRepository) {
        this.facturaRepository = facturaRepository;
        this.tipoPagoRepository = tipoPagoRepository;
    }

    public void registrarPago(Factura f, TipoPago p) {
    }

    public List<Factura> obtenerFacturasPendientes(int habitacion) {
        return null;
    }

    public Float calcularVuelto(Float total, List<TipoPago> pagos) {
        return 0.0f;
    }
}