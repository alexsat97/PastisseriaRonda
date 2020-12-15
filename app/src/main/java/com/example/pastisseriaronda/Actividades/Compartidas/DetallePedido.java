package com.example.pastisseriaronda.Actividades.Compartidas;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.pastisseriaronda.R;
import com.google.firebase.firestore.FirebaseFirestore;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class DetallePedido extends AppCompatActivity {
    TextView nom, est, num, dia, pet, pre, per;
    CardView confirmar;
    TextFieldBoxes preciotf;
    ExtendedEditText predioedt;
    AlertDialog dialog;
    ImageView imagen;
    Spinner s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);
        inicializar();
    }

    private void inicializar() {
        getSupportActionBar().hide();
        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage(R.string.gucam).setCancelable(false).build();
        imagen = findViewById(R.id.imagen);
        nom = findViewById(R.id.nombre);
        est = findViewById(R.id.est);
        num = findViewById(R.id.num);
        dia = findViewById(R.id.dia);
        pet = findViewById(R.id.pet);
        pre = findViewById(R.id.pre);
        confirmar = findViewById(R.id.confirmar);
        per = findViewById(R.id.per);
        s = findViewById(R.id.spinner);
        preciotf = findViewById(R.id.precio);
        predioedt = findViewById(R.id.pretext);

        if(getSharedPreferences("PR", Context.MODE_PRIVATE).getInt("empleado", 0) == 1){
            pre.setVisibility(View.GONE);
            est.setVisibility(View.GONE);
            per.setVisibility(View.VISIBLE);
            per.append(getIntent().getStringExtra("personal"));
            s.setVisibility(View.VISIBLE);
            s.setSelection(getIndex(s, getIntent().getStringExtra("estado")));
            predioedt.setVisibility(View.VISIBLE);
            preciotf.setVisibility(View.VISIBLE);
            predioedt.setText(String.valueOf(getIntent().getDoubleExtra("precio",0.0)));
        }else{
            confirmar.setVisibility(View.GONE);
            pre.append(String.valueOf(getIntent().getDoubleExtra("precio", 0.0)));
            est.append(getIntent().getStringExtra("estado"));
        }
        CircularProgressDrawable drawable = new CircularProgressDrawable(this);
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        drawable.start();
        Glide.with(this).load(getIntent().getStringExtra("foto"))
                .fallback(R.mipmap.ic_launcher_round)
                .placeholder(drawable)
                .skipMemoryCache(true)
                .centerCrop()
                .into(imagen);
        nom.setText(getIntent().getStringExtra("nombre"));
        num.append(String.valueOf(getIntent().getIntExtra("por", 1)));
        dia.append(getIntent().getStringExtra("dia"));
        pet.append(getIntent().getStringExtra("pet"));
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    public void confirmarCambios(View view) {
        dialog.show();
        FirebaseFirestore.getInstance().collection("Pedidos").document(getIntent().getStringExtra("key")).update(
                "estado", s.getSelectedItem().toString(),
                "precio", Double.valueOf(predioedt.getText().toString())
        ).addOnCompleteListener(task -> {
            dialog.dismiss();
            if(task.isSuccessful()) {
                Toasty.success(this, getString(R.string.camcor), Toast.LENGTH_SHORT, true).show();
                setResult(RESULT_OK);
                finish();
            }else
                Toasty.error(this, getString(R.string.comer), Toast.LENGTH_SHORT, true).show();
        });

    }

    public void volver(View view) {
        finish();
    }
}