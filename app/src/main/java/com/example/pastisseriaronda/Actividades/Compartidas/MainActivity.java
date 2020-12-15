package com.example.pastisseriaronda.Actividades.Compartidas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.pastisseriaronda.Fragments.Clientes.Inicio;
import com.example.pastisseriaronda.Fragments.Negocio.Mantenimiento;
import com.example.pastisseriaronda.Fragments.Compartidos.Pedidos;
import com.example.pastisseriaronda.Fragments.Compartidos.Perfil;
import com.example.pastisseriaronda.Fragments.Compartidos.Productos;
import com.example.pastisseriaronda.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    Productos p;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            initBottomBar(false);
        else
            setType();
    }


    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public void iniciarSesion(View view) {
        startActivity(new Intent(MainActivity.this, Login.class));
    }

    private void setType(){
        sharedPref = getSharedPreferences("PR", Context.MODE_PRIVATE);
        if(!sharedPref.contains("empleado") || sharedPref.getInt("empleado", 2) == 2 || sharedPref.getInt("empleado", 2) == 0 )
            initBottomBar(false);
        else
            initBottomBar(true);
        inicializarNotificaciones();
    }

    private void inicializarNotificaciones() {
        String col = "";
        if(sharedPref.getInt("empleado", 2) == 0)
            col = "TokensCLI";
        else
            col = "TokensEMP";
        String finalCol = col;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("token", instanceIdResult.getToken());
            FirebaseFirestore
                    .getInstance()
                    .collection(finalCol)
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(hm);
        });
    }

    public void initBottomBar(boolean empleado) {
        SmoothBottomBar b, bemp;
        b = findViewById(R.id.bottomBar);
        bemp = findViewById(R.id.bottomBaremp);
        if(!empleado) {
            bemp.setVisibility(View.GONE);
            b.setVisibility(View.VISIBLE);
            b.setItemActiveIndex(0);
            replaceFragment(new Inicio());
            b.setOnItemSelectedListener(i -> {
                switch (i) {
                    case 0:
                        replaceFragment(new Inicio());
                        break;
                    case 1:
                        if(p == null)
                            p = new Productos();
                        replaceFragment(p);
                        break;
                    case 2:
                        replaceFragment(new Pedidos());
                        break;
                    default:
                        replaceFragment(new Perfil());
                        break;
                }
                return true;
            });
        }else{
            bemp.setVisibility(View.VISIBLE);
            b.setVisibility(View.GONE);
            replaceFragment(new Pedidos());
            bemp.setOnItemSelectedListener(i -> {
                switch (i) {
                    case 0:
                        replaceFragment(new Pedidos());
                        break;
                    case 1:
                        if(p == null)
                            p = new Productos();
                        replaceFragment(p);
                        break;
                    case 2:
                        replaceFragment(new Mantenimiento());
                        break;
                    default:
                        replaceFragment(new Perfil());
                        break;
                }
                return true;
            });
        }
    }

}