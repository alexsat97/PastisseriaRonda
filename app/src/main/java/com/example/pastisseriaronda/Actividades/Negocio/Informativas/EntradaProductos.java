package com.example.pastisseriaronda.Actividades.Negocio.Informativas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.pastisseriaronda.Actividades.Negocio.Registro.RegistroEntradaProductos;
import com.example.pastisseriaronda.Auxiliares.Superclases.ListaRegistrosActivity;
import com.example.pastisseriaronda.R;

public class EntradaProductos extends ListaRegistrosActivity {

    private static final int CODIGO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_productos);
        inicializar();
        floatingActionButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(EntradaProductos.this, RegistroEntradaProductos.class), CODIGO);
        });
        obtenerColección("EntradaProductos");
        EditText eid = findViewById(R.id.id);
        eid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                registroMantenimientoAdapter.setCampo2(editable.toString());
                registroMantenimientoAdapter.filtrar();
                cambiarVista(registroMantenimientoAdapter.getItemCount()==0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO) {
            if (resultCode == RESULT_OK) {
                obtenerColección("EntradaProductos");
            }
        }
    }

    public void volver(View view) {finish(); }
}