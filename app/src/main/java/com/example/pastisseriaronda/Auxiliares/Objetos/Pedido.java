package com.example.pastisseriaronda.Auxiliares.Objetos;

public class Pedido {
    String nombrecliente, foto, nombre, estado, telefono, peticiones, uid;
    int porciones;
    double precio;
    long fecha;

    public Pedido(String nombrecliente, String foto, String nombre, String estado, String telefono, String peticiones, String uid, int porciones, double precio, long fecha) {
        this.nombrecliente = nombrecliente;
        this.foto = foto;
        this.nombre = nombre;
        this.estado = estado;
        this.telefono = telefono;
        this.peticiones = peticiones;
        this.uid = uid;
        this.porciones = porciones;
        this.precio = precio;
        this.fecha = fecha;
    }

    public Pedido() { }

    public String getNombrecliente() {
        return nombrecliente;
    }

    public void setNombrecliente(String nombrecliente) {
        this.nombrecliente = nombrecliente;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPeticiones() {
        return peticiones;
    }

    public void setPeticiones(String peticiones) {
        this.peticiones = peticiones;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPorciones() {
        return porciones;
    }

    public void setPorciones(int porciones) {
        this.porciones = porciones;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
