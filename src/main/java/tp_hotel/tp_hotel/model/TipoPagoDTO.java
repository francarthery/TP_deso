package tp_hotel.tp_hotel.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoPagoDTO {
    @NotNull
    MetodoPago metodoPago;

    @Positive
    Float importe;

    TipoMoneda tipoMoneda;
    @Positive
    Float cotizacion;
    @Size(max = 20)
    String numeroTarjeta;
    @Pattern(regexp = "^[a-zA-Z0-9ñÑÁÉÍÓÚÜáéíóúü.,&\\- ]+$") @Size(max = 100)
    String titular;
    @Future
    LocalDate fechaVencimiento;
    @Size(max = 4)
    String codigoSeguridad;
    @Size(max = 20) @Pattern(regexp = "^[0-9]+$")
    String numeroCheque;
    @Size(max = 50)
    String bancoEmisor;
    @Size(max = 50)
    String plaza;
    @FutureOrPresent
    LocalDate fechaCobro; 
}