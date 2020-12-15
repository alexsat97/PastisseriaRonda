package com.example.pastisseriaronda.Auxiliares.Objetos;

public class Usuario {
    private String nombre, telefono;
    private int tipo;

    public Usuario(String nombre, String telefono, int tipo) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
