package domain;

import java.time.LocalDate;

public class Huesped {
    private int id;
    private String apellido;
    private String nombres;
    private TipoDocumento documento;
    private String numeroDocumento;
    private String cuit;
    private IVA posicionFrenteAlIVA;
    private LocalDate fechaDeNacimiento;
    private String telefono;
    private String email;
    private String ocupacion;
    private String nacionalidad;
    private Direccion direccion;

    private Huesped(Builder builder){
        this.id = builder.id;
        this.apellido = builder.apellido;
        this.nombres = builder.nombres;
        this.documento = builder.documento;
        this.numeroDocumento = builder.numeroDocumento;
        this.cuit = builder.cuit;
        this.fechaDeNacimiento = builder.fechaDeNacimiento;
        this.telefono = builder.telefono;
        this.email = builder.email;
        this.ocupacion = builder.ocupacion;
        this.nacionalidad = builder.nacionalidad;
        this.posicionFrenteAlIVA = builder.posicionFrenteAlIVA;
        this.direccion = builder.direccion;
    }

    public static class Builder{
        private int id;
        private String apellido;
        private String nombres;
        private TipoDocumento documento;
        private String numeroDocumento;
        private String cuit;
        private IVA posicionFrenteAlIVA;
        private LocalDate fechaDeNacimiento;
        private String telefono;
        private String email;
        private String ocupacion;
        private String nacionalidad;
        private Direccion direccion;
      
        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder apellido(String apellido){
            this.apellido = apellido;
            return this;
        }

        public Builder nombres(String nombres){
            this.nombres = nombres;
            return this;
        }

        public Builder tipoDocumento(TipoDocumento tipo){
            this.documento = tipo;
            return this;
        }

        public Builder numeroDocumento(String numero){
            this.numeroDocumento = numero;
            return this;
        }

        public Builder cuit(String cuit){
            this.cuit = cuit;
            return this;
        }

        public Builder fechaDeNacimiento(LocalDate fecha){
            this.fechaDeNacimiento = fecha;
            return this;
        }

        public Builder telefono(String telefono){
            this.telefono = telefono;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder ocupacion(String ocupacion){
            this.ocupacion = ocupacion;
            return this;
        }

        public Builder nacionalidad(String nacionalidad){
            this.nacionalidad = nacionalidad;
            return this;
        }

        public Builder posicionFrenteAlIVA(IVA posicion){
            this.posicionFrenteAlIVA = posicion;
            return this;
        }

        public Builder direccion(Direccion direccion){
            this.direccion = direccion;
            return this;
        }
        
        public Huesped build(){
            return new Huesped(this);   
        }
    }

    public int getId(){
        return this.id;
    }

    public String getApellido(){
        return this.apellido;
    }

    public String getNombres(){
        return this.nombres;
    }

    public TipoDocumento getTipoDocumento(){
        return this.documento;
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

    public Direccion getDireccion(){
        return this.direccion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
    public void setTipoDocumento(TipoDocumento documento) {
        this.documento = documento;
    }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
    public void setPosicionFrenteAlIVA(IVA posicionFrenteAlIVA) {
        this.posicionFrenteAlIVA = posicionFrenteAlIVA;
    }
    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    
}
