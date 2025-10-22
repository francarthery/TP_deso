package tpdeso.domain;

public class UsuarioDTO {
    private int id;
    private String username;
    private String password;
    private Rol rol;

    public UsuarioDTO(int id, String username, String password, Rol rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Rol getRol() {
        return rol;
    }
}
