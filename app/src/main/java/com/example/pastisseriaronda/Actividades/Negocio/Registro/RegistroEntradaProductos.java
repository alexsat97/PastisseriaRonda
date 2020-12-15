package com.example.pastisseriaronda.Actividades.Negocio.Registro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;

import com.example.pastisseriaronda.Auxiliares.Superclases.GuardarRegistrosActivity;
import com.example.pastisseriaronda.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class RegistroEntradaProductos extends GuardarRegistrosActivity {
    private long fecha = -1;
    private Calendar c = Calendar.getInstance();
    private TextFieldBoxes ft, idt;
    private ExtendedEditText eft, eidt;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_productos);
        inicializar();
        ft = findViewById(R.id.fecha);
        idt = findViewById(R.id.id);
        eft = findViewById(R.id.fechatext);
        eidt = findViewById(R.id.idtext);

        ft.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                eft.setText(format.format(c.getTimeInMillis()));
                fecha = c.getTimeInMillis();
            };
            DatePickerDialog dp = new DatePickerDialog(this, R.style.DialogTheme, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dp.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            dp.show();
        });
        ft.setFocusable(false);
        eft.setFocusable(false);
    }

    public void guardarEntradaProducto(View view) {
        if(fecha == -1)
            ft.setError(getString(R.string.introfe), true);
        else if(eidt.getText().toString().isEmpty())
            idt.setError(getString(R.string.introid), true);
        else
            guardarRegistro("EntradaProductos", spinner.getSelectedItem().toString(),eidt.getText().toString(),fecha);
    }

    public void volver(View view) { finish(); }
}