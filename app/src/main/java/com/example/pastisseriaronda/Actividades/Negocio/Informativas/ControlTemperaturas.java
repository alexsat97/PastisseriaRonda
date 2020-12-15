package com.example.pastisseriaronda.Actividades.Negocio.Informativas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pastisseriaronda.Actividades.Negocio.Registro.RegistroTemperatura;
import com.example.pastisseriaronda.Auxiliares.Superclases.ListaRegistrosActivity;
import com.example.pastisseriaronda.R;

public class ControlTemperaturas extends ListaRegistrosActivity {

    private static final int CODIGO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_temperaturas);
        inicializar();
        floatingActionButton.setOnClickListener(v -> { startActivityForResult(new Intent(ControlTemperaturas.this, RegistroTemperatura.class), CODIGO); });
        obtenerColección("Temperaturas");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO) {
            if (resultCode == RESULT_OK) {
                obtenerColección("Temperaturas");
            }
        }
    }

    public void volver(View view) { finish(); }
}