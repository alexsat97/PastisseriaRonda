package com.example.pastisseriaronda.Auxiliares.Superclases;

import android.app.AlertDialog;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pastisseriaronda.Auxiliares.Objetos.RegistroMantenimiento;
import com.example.pastisseriaronda.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class GuardarRegistrosActivity extends AppCompatActivity {
    protected AlertDialog dialog;
    protected Spinner spinner;

    protected void inicializar(){
        getSupportActionBar().hide();
        spinner = findViewById(R.id.spinner);
        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage(R.string.gure).setCancelable(false).build();
    }

    protected void guardarRegistro(String col, String campo1, String campo2, long campo3) {
        dialog.show();
        FirebaseFirestore.getInstance().collection(col).add(new RegistroMantenimiento(campo1,campo2, campo3, new Date().getTime())).addOnCompleteListener(task -> {
            dialog.dismiss();
            if(task.isSuccessful()){
                Toasty.success(this, getString(R.string.guacorr), Toast.LENGTH_SHORT, true).show();
                setResult(RESULT_OK);
                finish();
            }else{
                Toasty.error(this, getString(R.string.errorsu), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}
