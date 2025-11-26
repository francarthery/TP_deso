package tp_hotel.tp_hotel.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp_hotel.tp_hotel.model.Consumo;
import tp_hotel.tp_hotel.model.Estadia;
import tp_hotel.tp_hotel.model.Factura;
import tp_hotel.tp_hotel.model.NotaCredito;
import tp_hotel.tp_hotel.model.PersonaJuridica;
import tp_hotel.tp_hotel.repository.FacturaRepository;

@Service
public class GestorFacturacion {

    private final FacturaRepository facturaRepository;

    @Autowired
    public GestorFacturacion(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public void generarFactura(Estadia e) {
    }

    public void anularFactura(NotaCredito n) {
    }

    public void facturarConsumosSeparados(int estadiaId, List<Consumo> consumos, PersonaJuridica responsable) {
    }

    public NotaCredito emitirNotaCredito(List<Factura> facturas) {
        return null;
    }
}