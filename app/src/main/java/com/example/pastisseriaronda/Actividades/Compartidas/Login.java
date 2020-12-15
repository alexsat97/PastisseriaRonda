package com.example.pastisseriaronda.Actividades.Compartidas;

import androidx.annotation.NonNull;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private TextFieldBoxes mail, pass;
    private ExtendedEditText tpass, tmail;
    String m;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        getSupportActionBar().hide();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        tpass = findViewById(R.id.passtext);
        tmail = findViewById(R.id.mailtext);
        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage(R.string.initses).setCancelable(false).build();
    }

    public void registro(View view) {
        startActivity(new Intent(Login.this, Registro.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toasty.error(getApplicationContext(), getString(R.string.errorses), Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        dialog.show();
                        if(task.getResult().getAdditionalUserInfo().isNewUser()){
                            Usuario user = new Usuario(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName(), "000000000", 0);
                            FirebaseDatabase.getInstance().getReference("Usuarios")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Toasty.error(Login.this, getString(R.string.errorses), Toast.LENGTH_SHORT, true).show();
                                            dialog.dismiss();
                                        }else {
                                            SharedPreferences sharedPref = getSharedPreferences("PR", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putInt("empleado", 0);
                                            editor.putString("telf", "000000000");
                                            editor.putString("nom", GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName());
                                            editor.apply();
                                            dialog.dismiss();
                                            finish();
                                            Toasty.success(Login.this,  getString(R.string.sescor), Toast.LENGTH_SHORT, true).show();
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                        }
                                    });
                        }else{
                            verifyUser();
                        }
                    } else {
                        Toasty.error(Login.this,  getResources().getString(R.string.errorses), Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void olvidado(View view) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        else
            m = tmail.getText().toString();
        if(m.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(m).matches()) {
            mail.setError(getString(R.string.emailerror), true);
        }else{
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.recc)
                        .setPositiveButton(R.string.si, (dialog, id) -> {
                            enviarCorreo();
                        })
                        .setNegativeButton(R.string.can, (dialog, id) -> {
                        }).create().show();
            }else
                enviarCorreo();
        }
    }

    private void enviarCorreo(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(m)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toasty.info(getApplicationContext(), getString(R.string.recemail), Toast.LENGTH_SHORT, true).show();
                        if(FirebaseAuth.getInstance().getCurrentUser() != null){
                            SharedPreferences sharedPref = getSharedPreferences("PR", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("empleado", 2);
                            editor.apply();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                    }
                });
    }
    public void login(View view) {
        String m = tmail.getText().toString();
        String p = tpass.getText().toString();
        if (m.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(m).matches()){
            mail.setError(getString(R.string.emailerror), true);
            return;
        }
        if (p.length()<6){
            pass.setError(getString(R.string.passerr), true);
            return;
        }
        dialog.show();
        mAuth.signInWithEmailAndPassword(m,p)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        verifyUser();
                    } else {
                        dialog.dismiss();
                        Toasty.error(Login.this,  getString(R.string.errorses), Toast.LENGTH_SHORT, true).show();
                    }
                });

    }

    public void verifyUser(){
        FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPref = getSharedPreferences("PR", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("empleado", dataSnapshot.child("tipo").getValue(Integer.class));
                editor.putString("telf", dataSnapshot.child("telefono").getValue(String.class));
                editor.putString("nom", dataSnapshot.child("nombre").getValue(String.class));
                editor.apply();
                dialog.dismiss();
                finish();
                Toasty.success(Login.this,  getString(R.string.sescor), Toast.LENGTH_SHORT, true).show();
                startActivity(new Intent(Login.this, MainActivity.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}