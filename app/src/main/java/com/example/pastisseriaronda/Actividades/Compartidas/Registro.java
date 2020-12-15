package com.example.pastisseriaronda.Actividades.Compartidas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.pastisseriaronda.Auxiliares.Objetos.Usuario;
import com.example.pastisseriaronda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class Registro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextFieldBoxes name, mail, pass, rpass, tel;
    private ExtendedEditText tname, tmail, tpass, trpass, ttel;
    private String n, m, p, t;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        inicializar();
    }

    private void inicializar(){
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        name = findViewById(R.id.nombre);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        rpass = findViewById(R.id.rpass);
        tel = findViewById(R.id.telf);
        tname = findViewById(R.id.nombretext);
        tmail = findViewById(R.id.mailtext);
        tpass = findViewById(R.id.passtext);
        trpass = findViewById(R.id.rpasstext);
        ttel = findViewById(R.id.telftext);
        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage(R.string.creusu).setCancelable(false).build();
    }

    private boolean validate(){
        n = tname.getText().toString();
        m = tmail.getText().toString();
        p = tpass.getText().toString();
        t = ttel.getText().toString();
        if (n.isEmpty()) {
            name.setError(getString(R.string.namva), true);
            return false;
        }

        if (m.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(m).matches()) {
            mail.setError(getString(R.string.emailerror), true);
            return false;
        }

        if(p.isEmpty()){
            pass.setError(getString(R.string.contemp), true);
            return false;
        }

        if(trpass.getText().toString().isEmpty()){
            rpass.setError(getString(R.string.contemp), true);
            return false;
        }

        if(!p.equals(trpass.getText().toString())){
            rpass.setError(getString(R.string.samecont), true);
            return false;
        }

        if(p.length() < 6){
            rpass.setError(getString(R.string.longcont), true);
            return false;
        }


        if(t.length() != 9){
            tel.setError(getString(R.string.telfval), true);
            return false;
        }

        return true;
    }

    public void efectuarRegistro(View view) {
        if(validate()){
            dialog.show();
            mAuth.createUserWithEmailAndPassword(m, p)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            addDB();
                        } else {
                            dialog.dismiss();
                            Toasty.error(Registro.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    });
        }
    }

    private void addDB() {
        Usuario user = new Usuario(n, t, 0);
        FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(user).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toasty.error(Registro.this,  getString(R.string.noreg),
                                Toast.LENGTH_SHORT, true).show();
                        dialog.dismiss();
                    }else {
                        Toasty.success(Registro.this, getString(R.string.regcor),
                                Toast.LENGTH_SHORT, true)
                                .show();
                        SharedPreferences sharedPref = getSharedPreferences("PR", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("empleado", 0);
                        editor.putString("telf", t);
                        editor.putString("nom", n);
                        editor.apply();
                        dialog.dismiss();
                        finishAffinity();
                        startActivity(new Intent(Registro.this, MainActivity.class));
                    }
                });
    }
}