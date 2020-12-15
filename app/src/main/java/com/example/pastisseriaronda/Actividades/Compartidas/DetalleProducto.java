package com.example.pastisseriaronda.Actividades.Compartidas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pastisseriaronda.R;
import com.google.firebase.auth.FirebaseAuth;

public class DetalleProducto extends AppCompatActivity {
    ImageView image;
    TextView desc, alerg, nom;
    String n, f, a, d;
    CardView boton;
    boolean pedido = false;
    double p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        getSupportActionBar().hide();
        Bundle extras = getIntent().getExtras();
        boton = findViewById(R.id.reservar);
        n = extras.getString("nombre");
        f = extras.getString("foto");
        d = extras.getString("desc");
        a = extras.getString("aler");
        p = extras.getDouble("precio");
        pedido = extras.getBoolean("bpedido");
        nom = findViewById(R.id.nombre);
        image = findViewById(R.id.imagen);
        desc = findViewById(R.id.desc);
        alerg = findViewById(R.id.alergenos);
        if(!pedido)
            boton.setVisibility(View.GONE);
        CircularProgressDrawable drawable = new CircularProgressDrawable(this);
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        drawable.start();
        Glide.with(this).load(f)
                .fallback(R.mipmap.ic_launcher_round)
                .placeholder(drawable)
                .skipMemoryCache(true)
                .centerCrop()
                .into(image);
        desc.append(d);
        alerg.append(a);
        nom.setText(n);
    }

    public void reservar(View view) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent i = new Intent(this, InformacionReserva.class);
            i.putExtra("nombre", n);
            i.putExtra("foto", f);
            i.putExtra("precio", p);
            startActivity(i);
        }else{
            startActivity(new Intent(this, Login.class));
        }
    }

    public void volver(View view) {
        finish();
    }
}