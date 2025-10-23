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

    public Direccion(Builder builder){
        this.calle = builder.calle;
        this.numero = builder.numero;
        this.departamento = builder.departamento;
        this.piso = builder.piso;
        this.codigoPostal = builder.codigoPostal;
        this.localidad = builder.localidad;
        this.provincia = builder.provincia;
        this.pais = builder.pais;
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

    public static class Builder {
        private String calle;
        private String numero;
        private String departamento;
        private String piso;
        private String codigoPostal;
        private String localidad;
        private String provincia;
        private String pais;

        public Builder calle(String calle){
            this.calle = calle;
            return this;
        }

        public Builder numero(String numero){
            this.numero = numero;
            return this;
        }

        public Builder departamento(String departamento){
            this.departamento = departamento;
            return this;
        }

        public Builder piso(String piso){
            this.piso = piso;
            return this;
        }

        public Builder codigoPostal(String codigoPostal){
            this.codigoPostal = codigoPostal;
            return this;
        }

        public Builder localidad(String localidad){
            this.localidad = localidad;
            return this;
        }

        public Builder provincia(String provincia){
            this.provincia = provincia;
            return this;
        }

        public Builder pais(String pais){
            this.pais = pais;
            return this;
        }

        public Direccion build(){
            return new Direccion(this);
        }
    }

}


