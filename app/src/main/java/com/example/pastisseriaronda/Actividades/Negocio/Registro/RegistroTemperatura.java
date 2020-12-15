package com.example.pastisseriaronda.Actividades.Negocio.Registro;

import android.os.Bundle;
import android.view.View;

import com.example.pastisseriaronda.Auxiliares.Superclases.GuardarRegistrosActivity;
import com.example.pastisseriaronda.R;
import com.ramotion.fluidslider.FluidSlider;

public class RegistroTemperatura extends GuardarRegistrosActivity {
    private FluidSlider numberPicker;
    int max = 20, min = -20, total = max-min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_temperatura);
        inicializar();
        numberPicker = findViewById(R.id.fluid);
        numberPicker.setStartText(min + " ºC");
        numberPicker.setEndText(max + " ºC");
        numberPicker.setPosition((float) 0.5);
        numberPicker.setBubbleText("0 ºC");
        numberPicker.setPositionListener(aFloat -> {
            numberPicker.setBubbleText(Math.round(min + (total  * numberPicker.getPosition())) + " ºC");
            return null;
        });
    }

    public void guardarTemperatura(View view) { guardarRegistro("Temperaturas", spinner.getSelectedItem().toString(),Math.round(min + (total  * numberPicker.getPosition())) + " ºC",-1); }

    public void volver(View view) { finish();}
}