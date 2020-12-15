package com.example.pastisseriaronda.Auxiliares.Objetos;

public class Producto {
    String nombre, descripcion, alergenos, tipo, subtipo, foto;
    boolean bajopedido, disponible;
    double precio;

    public Producto() { }

    public Producto(String nombre, String descripcion, String alergenos, String tipo, String subtipo, String foto, boolean bajopedido, boolean disponible, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.alergenos = alergenos;
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.foto = foto;
        this.bajopedido = bajopedido;
        this.disponible = disponible;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(String alergenos) {
        this.alergenos = alergenos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isbajopedido() {
        return bajopedido;
    }

    public void setbajopedido(boolean bajopedido) {
        this.bajopedido = bajopedido;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
