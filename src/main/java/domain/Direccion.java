package domain;

public class Direccion {
    private String calle;
    private String numero;
    private String departamento;
    private String piso;
    private String codigoPostal;
    private String localidad;
    private String provincia;
    private String pais;

    public Direccion(String calle, String numero, String departamento, String piso, String codigoPostal, String localidad, String provincia, String pais){
        this.calle = calle;
        this.numero = numero;
        this.departamento = departamento;
        this.piso = piso;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
    }

    public String getDepartamento(){
        return this.departamento;
    }

    public String getPiso(){
        return this.piso;
    }

    public String getCodigoPostal(){
        return this.codigoPostal;
    }

    public String getLocalidad(){
        return this.localidad;
    }

    public String getProvincia(){
        return this.provincia;
    }

    public String getPais(){
        return this.pais;
    }

    public String getCalle(){
        return this.calle;
    }
    public String getNumero(){
        return this.numero;
    }

    public void setCalle(String calle){
        this.calle = calle;
    }
    public void setNumero(String numero){
        this.numero = numero;
    }

    public void setDepartamento(String departamento){
        this.departamento = departamento;
    }
    
    public void setPiso(String piso){
        this.piso = piso;
    }

    public void setCodigoPostal(String codigoPostal){
        this.codigoPostal = codigoPostal;
    }

    public void setLocalidad(String localidad){
        this.localidad = localidad;
    }

    public void setProvincia(String provincia){
        this.provincia = provincia;
    }

    public void setPais(String pais){
        this.pais = pais;
    }


}


