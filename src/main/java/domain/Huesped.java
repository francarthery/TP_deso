package domain;

import java.util.Date;

public class Huesped {
    private String apellido;
    private String nombre;
    private TipoDocumento documento;
    private String numeroDocumento;
    private String cuit;
    private IVA posicionFrenteAlIVA;
    private Date fechaDeNacimiento;
    private String telefono;
    private String email;
    private String ocupacion;
    private String nacionalidad;

    private Huesped(Builder builder){
        this.apellido = builder.apellido;
        this.nombre = builder.nombre;
        this.documento = builder.documento;
        this.numeroDocumento = builder.numeroDocumento;
        this.cuit = builder.cuit;
        this.fechaDeNacimiento = builder.fechaDeNacimiento;
        this.telefono = builder.telefono;
        this.email = builder.email;
        this.ocupacion = builder.ocupacion;
        this.nacionalidad = builder.nacionalidad;
        this.posicionFrenteAlIVA = builder.posicionFrenteAlIVA;
    }

    public static class Builder{
        private String apellido;
        private String nombre;
        private TipoDocumento documento;
        private String numeroDocumento;
        private String cuit;
        private IVA posicionFrenteAlIVA;
        private Date fechaDeNacimiento;
        private String telefono;
        private String email;
        private String ocupacion;
        private String nacionalidad;


        //Getters
        public String getApellido(){
            return this.apellido;
        }

        public String getNombre(){
            return this.nombre;
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

        public Date getFechaDeNacimiento(){
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
        
        //Builder 
        public Builder apellido(String apellido){
            this.apellido = apellido;
            return this;
        }

        public Builder nombre(String nombre){
            this.nombre = nombre;
            return this;
        }

        public Builder documento(TipoDocumento tipo){
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

        public Builder fechaDeNacimiento(Date fecha){
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
        
        public Huesped build(){
            return new Huesped(this);   
        }


    }
}
