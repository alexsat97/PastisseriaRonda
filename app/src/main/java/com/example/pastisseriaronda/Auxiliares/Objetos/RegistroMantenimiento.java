package com.example.pastisseriaronda.Auxiliares.Objetos;

public class RegistroMantenimiento {
    String campo1, campo2;
    long caducidad;
    long fecha;

    public RegistroMantenimiento() { }

    public RegistroMantenimiento(String campo1, String campo2, long caducidad, long fecha) {
        this.campo1 = campo1;
        this.campo2 = campo2;
        this.caducidad = caducidad;
        this.fecha = fecha;
    }

    public String getCampo1() {
        return campo1;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public String getCampo2() {
        return campo2;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }

    public long getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(long caducidad) {
        this.caducidad = caducidad;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}