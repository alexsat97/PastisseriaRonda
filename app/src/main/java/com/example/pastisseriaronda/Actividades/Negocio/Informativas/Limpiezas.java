package com.example.pastisseriaronda.Actividades.Negocio.Informativas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pastisseriaronda.Actividades.Negocio.Registro.RegistroLimpieza;
import com.example.pastisseriaronda.Auxiliares.Superclases.ListaRegistrosActivity;
import com.example.pastisseriaronda.R;

public class Limpiezas extends ListaRegistrosActivity {

    private static final int CODIGO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limpiezas);
        inicializar();
        floatingActionButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(Limpiezas.this, RegistroLimpieza.class), CODIGO);
        });
        obtenerColección("Limpiezas");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO) {
            if (resultCode == RESULT_OK) { obtenerColección("Limpiezas"); }
        }
    }

    public void volver(View view) { finish(); }
}