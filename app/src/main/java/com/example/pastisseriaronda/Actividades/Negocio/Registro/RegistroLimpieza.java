package com.example.pastisseriaronda.Actividades.Negocio.Registro;

import android.os.Bundle;
import android.view.View;

import com.example.pastisseriaronda.Auxiliares.Superclases.GuardarRegistrosActivity;
import com.example.pastisseriaronda.R;

public class RegistroLimpieza extends GuardarRegistrosActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_limpieza);
        inicializar();
    }

    public void guardarLimpieza(View view) { guardarRegistro("Limpiezas", spinner.getSelectedItem().toString(),"",-1); }

    public void volver(View view) { finish(); }
}