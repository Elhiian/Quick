package com.app.elhiian.quick.clases;

public class UsuarioPrestadorServicio {
    private String idUsuario;
    private String fechaNacimiento;
    private String domicilio;
    private String tipoLicencia;
    private String tipoServicio;
    private String estadoConductorServicio;
    private String nombre;
    private String celular;

    public UsuarioPrestadorServicio() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getEstadoConductorServicio() {
        return estadoConductorServicio;
    }

    public void setEstadoConductorServicio(String estadoConductorServicio) {
        this.estadoConductorServicio = estadoConductorServicio;
    }
}
