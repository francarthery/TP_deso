package domain;

import java.time.LocalDate;

public class HuespedDTO {
    private int id;
    private String apellido;
    private String nombres;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String cuit;
    private IVA posicionFrenteAlIVA;
    private LocalDate fechaDeNacimiento;
    private String telefono;
    private String email;
    private String ocupacion;
    private String nacionalidad;

    public HuespedDTO(int id, String nombres, String apellido, TipoDocumento tipoDocumento, String numeroDocumento, String cuit, IVA posicionFrenteAlIVA, LocalDate fechaDeNacimiento, String telefono, String email, String ocupacion, String nacionalidad) {
        this.id = id;
        this.nombres = nombres;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.cuit = cuit;
        this.posicionFrenteAlIVA = posicionFrenteAlIVA;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
        this.nacionalidad = nacionalidad;
    }

    public int getId() {
        return id;
    }

    public String getApellido(){
        return this.apellido;
    }

    public String getNombres(){
        return this.nombres;
    }

    public TipoDocumento getTipoDocumento(){
        return this.tipoDocumento;
    }

    public String getNumeroDocumento(){
        return this.numeroDocumento;
    }

    public String getCuit(){
        return this.cuit;
    }

    public LocalDate getFechaDeNacimiento(){
        return this.fechaDeNacimiento;
    }

    public String getTelefono(){
        return this.telefono;
    }

    public String getEmail(){
        return this.email;
    }

    public String getOcupacion(){
        return this.ocupacion;
    }

    public String getNacionalidad(){
        return this.nacionalidad;
    }

    public IVA getPosicionFrenteAlIVA(){
        return this.posicionFrenteAlIVA;
    }
}
