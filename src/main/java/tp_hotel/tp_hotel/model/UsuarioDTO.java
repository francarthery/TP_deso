package tp_hotel.tp_hotel.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private int id;

    @Size(min = 1, max = 30) @NotNull
    private String username;

    @Size(min = 8, max = 100) @NotNull
    private String password;

    @NotNull
    private Rol rol;
}
