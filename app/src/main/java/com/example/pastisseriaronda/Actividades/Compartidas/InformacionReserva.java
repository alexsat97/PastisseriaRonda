package com.example.pastisseriaronda.Actividades.Compartidas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pastisseriaronda.Auxiliares.Objetos.Pedido;
import com.example.pastisseriaronda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class InformacionReserva extends AppCompatActivity {
    NumberPicker numberPicker;
    TextFieldBoxes ft, nombre, telf;
    ExtendedEditText eft, numt, peti, nomt;
    LinearLayout l1, l2, l3, l4;
    TextView nom, por, dia, num, pet, pre, nomc, nomcli, extended, gra, rec, id;
    private AlertDialog dialog;
    private long fecha = -1;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_reserva);
        getSupportActionBar().hide();
        inicializar();

    }

    private void inicializar() {
        numberPicker = findViewById(R.id.number);
        ft = findViewById(R.id.fecha);
        eft = findViewById(R.id.fechatext);
        nom = findViewById(R.id.nombre);
        por = findViewById(R.id.por);
        dia = findViewById(R.id.dia);
        num = findViewById(R.id.num);
        pet = findViewById(R.id.peti);
        pre = findViewById(R.id.pre);
        numt = findViewById(R.id.telftext);
        peti = findViewById(R.id.pettext);
        nombre = findViewById(R.id.nom);
        telf = findViewById(R.id.telf);
        nomt = findViewById(R.id.nomt);
        nomc = findViewById(R.id.nomcli);
        nomcli = findViewById(R.id.clinomt);
        extended = findViewById(R.id.extended);
        gra = findViewById(R.id.gracias);
        rec = findViewById(R.id.rec);
        id = findViewById(R.id.idped);
        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage(R.string.relpe).setCancelable(false).build();

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        l4 = findViewById(R.id.l4);

        if(getSharedPreferences("PR", Context.MODE_PRIVATE).getInt("empleado", 0) == 1)
            nombre.setVisibility(View.VISIBLE);
        else
            numt.setText(getSharedPreferences("PR", Context.MODE_PRIVATE).getString("telf", ""));
        initNumberPicker();
        initCalendar();
    }

    private void initCalendar() {
        ft.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                eft.setText(format.format(c.getTimeInMillis()));
                fecha = c.getTimeInMillis();
            };
            DatePickerDialog dp = new DatePickerDialog(this, R.style.DialogTheme, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dp.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() + 345600000);
            dp.show();
        });
        ft.setFocusable(false);
        eft.setFocusable(false);
    }

    private void initNumberPicker() {
       numberPicker.setMinValue(1);
       numberPicker.setMaxValue(12);
    }

    public void continuar(View view) {
        if(fecha == -1) {
            ft.setError("Introduzca una fecha.", true);
            return;
        }
        if(numt.getText().toString().length() != 9){
            telf.setError("Introduzca un número de telefono válido.", true);
            return;
        }
        if(getSharedPreferences("PR", Context.MODE_PRIVATE).getInt("empleado", 0) == 1 && nomt.getText().toString().isEmpty()){
            nombre.setError("Introduzca un nombre", true);
            return;
        }
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.VISIBLE);
    }

    public void volver(View view) {
        finish();
    }

    public void continuarReserva(View view) {
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.VISIBLE);
        nom.setText(getIntent().getStringExtra("nombre"));
        if(numberPicker.getValue() > 1)
            por.setText(numberPicker.getValue() + getString(R.string.por));
        else
            por.setText(numberPicker.getValue() + getString(R.string.porc));
        dia.setText(format.format(fecha));
        num.setText(numt.getText().toString());
        if(peti.getText().toString().isEmpty())
            pet.setText(R.string.nopetes);
        else
            pet.setText(peti.getText().toString());
        pre.setText(getIntent().getDoubleExtra("precio", 0.0) * numberPicker.getValue() + " €*");
        if(getSharedPreferences("PR", Context.MODE_PRIVATE).getInt("empleado", 0) == 1){
            extended.setVisibility(View.GONE);
            nomcli.setVisibility(View.VISIBLE);
            nomc.setVisibility(View.VISIBLE);
            nomcli.setText(nomt.getText().toString());
        }
    }

    public void volverAnterior(View view) {
        l2.setVisibility(View.GONE);
        l1.setVisibility(View.VISIBLE);
    }

    public void volverAnteriorRes(View view) {
        l3.setVisibility(View.GONE);
        l2.setVisibility(View.VISIBLE);
    }

    public void finalizarReserva(View view) {
        Pedido p;
        if(getSharedPreferences("PR", Context.MODE_PRIVATE).getInt("empleado", 0) == 1){
            p = new Pedido(nomcli.getText().toString(),
                    getIntent().getStringExtra("foto"),
                    getIntent().getStringExtra("nombre"),
                    "En proceso",
                    numt.getText().toString(),
                    pet.getText().toString(),
                    "",
                    numberPicker.getValue(),
                    getIntent().getDoubleExtra("precio", 0.0) * numberPicker.getValue(),
                    fecha);
            rec.setVisibility(View.GONE);
            gra.setVisibility(View.GONE);
        }else{
            p = new Pedido(getSharedPreferences("PR", Context.MODE_PRIVATE).getString("nom", ""),
                    getIntent().getStringExtra("foto"),
                    getIntent().getStringExtra("nombre"),
                    "En proceso",
                    numt.getText().toString(),
                    pet.getText().toString(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    numberPicker.getValue(),
                    getIntent().getDoubleExtra("precio", 0.0) * numberPicker.getValue(),
                    fecha);
        }
        dialog.show();
        FirebaseFirestore.getInstance().collection("Pedidos").add(p).addOnCompleteListener(task -> {
            dialog.dismiss();
            if(task.isSuccessful()){
                l4.setVisibility(View.VISIBLE);
                l3.setVisibility(View.GONE);
                id.append(task.getResult().getId());
            }else{
                Toasty.error(getApplicationContext(), "No se ha podido realizar el pedido en este momento! Intentelo más tarde.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void cerrar(View view) {
        finish();
    }
}