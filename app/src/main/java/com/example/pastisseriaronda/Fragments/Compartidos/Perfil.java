package com.example.pastisseriaronda.Fragments.Compartidos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.pastisseriaronda.Actividades.Compartidas.MainActivity;
import com.example.pastisseriaronda.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class Perfil extends Fragment {
    FirebaseAuth mAuth;
    GoogleSignInAccount account;
    SharedPreferences sharedPref;
    private AlertDialog dialog;


    public Perfil() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        dialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.Custom).setMessage(R.string.gucam).setCancelable(false).build();
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout nl = view.findViewById(R.id.pnl);
        LinearLayout l = view.findViewById(R.id.pl);

        sharedPref = getActivity().getSharedPreferences("PR", Context.MODE_PRIVATE);
        int type = sharedPref.getInt("empleado", 2);
        if(type != 2){
            nl.setVisibility(View.GONE);
            l.setVisibility(View.VISIBLE);
            addInfo(view);
        }else{
            nl.setVisibility(View.VISIBLE);
            l.setVisibility(View.GONE);
        }

    }

    private void addInfo(View v) {
        TextFieldBoxes mail, tel, nom;
        ExtendedEditText tmail, ttelf, tnom;
        CardView cv = v.findViewById(R.id.cerses);
        CardView rc = v.findViewById(R.id.rescont);
        CardView gc = v.findViewById(R.id.guardar);
        mail = v.findViewById(R.id.mail);
        tel = v.findViewById(R.id.telf);
        nom = v.findViewById(R.id.nombre);
        tmail = v.findViewById(R.id.mailtext);
        ttelf = v.findViewById(R.id.telftext);
        tnom = v.findViewById(R.id.nombretext);

        cv.setOnClickListener(v1 -> Perfil.this.cerrarSesión());
        rc.setOnClickListener(v1 -> FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toasty.info(getContext(), getString(R.string.recemail), Toast.LENGTH_SHORT, true).show();
                        cerrarSesión();
                    }
                }));
        gc.setOnClickListener(v1 -> {
                    if (ttelf.getText().toString().length() != 9) {
                        tel.setError(getString(R.string.telfval), true);
                        return;
                    }
                    if (tnom.getText().toString().isEmpty()) {
                        nom.setError(getString(R.string.namva), true);
                        return;
                    }
                    dialog.show();

                    SharedPreferences sharedPref = getActivity().getSharedPreferences("PR", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    boolean ct = !ttelf.getText().toString().equals(sharedPref.getString("telf", "000000000"));
                    boolean cn = !tnom.getText().toString().equals(sharedPref.getString("nom", ""));

                    if (ct) {
                        FirebaseDatabase.getInstance().getReference("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("telefono").setValue(ttelf.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                editor.putString("telf", ttelf.getText().toString());
                                editor.apply();
                                if (cn) {
                                    cambioNombre(tnom.getText().toString(), editor, gc);
                                } else {
                                    gc.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    Toasty.success(getContext(), getString(R.string.guacor), Toast.LENGTH_SHORT, true).show();
                                }
                            } else {
                                dialog.dismiss();
                                Toasty.error(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT, true).show();
                            }
                        });

                    }else{
                        cambioNombre(tnom.getText().toString(), editor, gc);
                    }
        });

        mail.setEnabled(false);
        tmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        tnom.setText(sharedPref.getString("nom", ""));
        ttelf.setText(sharedPref.getString("telf", "000000000"));

        tnom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(sharedPref.getString("nom", "")) || !ttelf.getText().toString().equals(sharedPref.getString("telf", "000000000")))
                    gc.setVisibility(View.VISIBLE);
                else
                    gc.setVisibility(View.GONE);
            }
        });

        ttelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(sharedPref.getString("telf", "000000000")) || !tnom.getText().toString().equals(sharedPref.getString("nom", "")))
                    gc.setVisibility(View.VISIBLE);
                else
                    gc.setVisibility(View.GONE);
            }
        });
    }

    private void cambioNombre(String nombre, SharedPreferences.Editor editor, CardView gc) {
        FirebaseDatabase.getInstance().getReference("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("nombre").setValue(nombre).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                editor.putString("nom", nombre);
                editor.apply();
                dialog.dismiss();
                gc.setVisibility(View.GONE);
                Toasty.success(getContext(), getString(R.string.guacor), Toast.LENGTH_SHORT, true).show();
            }else{
                dialog.dismiss();
                Toasty.error(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void cerrarSesión(){
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.cers)
                .setPositiveButton(R.string.si, (dialog, id) -> {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("empleado", 2);
                    editor.apply();
                    FirebaseAuth.getInstance().signOut();
                    Toasty.success(getContext(), getString(R.string.cerrses), Toasty.LENGTH_SHORT, true).show();
                    MainActivity ma = (MainActivity) getActivity();
                    ma.initBottomBar(false);
                })
                .setNegativeButton(R.string.can, (dialog, id) -> {
                }).create().show();
    }
}