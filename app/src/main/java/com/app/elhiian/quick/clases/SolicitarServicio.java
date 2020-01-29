package com.app.elhiian.quick.clases;

public class SolicitarServicio {
    private String id;
    private String idUsuario;
    private String nombres;
    private String tipoServicio;
    private String origenLatitud;
    private String origenLongitud;
    private String tipoUbicacion;
    private String direccionServicio;
    private String distancia;
    private String destino;
    private String estado;
    private String idConductor;
    private String fecha;


    public SolicitarServicio() {
    }

    public String getId() {
        return id;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getOrigenLatitud() {
        return origenLatitud;
    }

    public void setOrigenLatitud(String origenLatitud) {
        this.origenLatitud = origenLatitud;
    }

    public String getOrigenLongitud() {
        return origenLongitud;
    }

    public void setOrigenLongitud(String origenLongitud) {
        this.origenLongitud = origenLongitud;
    }

    public String getTipoUbicacion() {
        return tipoUbicacion;
    }

    public void setTipoUbicacion(String tipoUbicacion) {
        this.tipoUbicacion = tipoUbicacion;
    }

    public String getDireccionServicio() {
        return direccionServicio;
    }

    public void setDireccionServicio(String direccionServicio) {
        this.direccionServicio = direccionServicio;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
