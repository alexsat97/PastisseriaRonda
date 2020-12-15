package com.example.pastisseriaronda.Fragments.Clientes;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pastisseriaronda.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.CALL_PHONE;

public class Inicio extends Fragment {
    TextView estado;
    CardView mapa, llamar;
    ImageView facebook, instagram;
    SimpleDateFormat f1 = new SimpleDateFormat("HH:mm");

    public Inicio() { }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inicio, container, false);
        inicializar(v);
        return v;
    }

    private void inicializar(View v) {
        estado = v.findViewById(R.id.esta);
        mapa = v.findViewById(R.id.map);
        llamar = v.findViewById(R.id.llamar);
        facebook = v.findViewById(R.id.facebook);
        instagram = v.findViewById(R.id.instagram);
        
        obtenerEstado();
        inicializarBotones();
        inicializarRRSS();
    }

    private void obtenerEstado() {
        boolean hora = false;
        try {
            hora = evaluarHora();
        } catch (ParseException e) { }
        evaluarDia();
        if(hora && evaluarDia()){
            estado.setText(R.string.ab);
        }else{
            estado.setText(R.string.ce);
        }
    }

    private boolean evaluarHora() throws ParseException {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(f1.parse("09:00"));
        calendar1.add(Calendar.DATE, 1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(f1.parse("14:00"));
        calendar2.add(Calendar.DATE, 1);

        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(f1.parse(f1.format(new Date())));
        calendar3.add(Calendar.DATE, 1);

        Date x = calendar3.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime()))
            return true;
        return false;
    }

    private boolean evaluarDia() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != 2;
    }

    private void inicializarBotones() {
        mapa.setOnClickListener(v -> {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=qwerty&query_place_id=ChIJX8eKHhbgphIRjYAYXNzPbTE"));
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(mapIntent);
            else
                Toasty.error(getContext(), "Para poder visualizarlo debe tener instalado Google Maps.", Toast.LENGTH_SHORT, true).show();
        });
        llamar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+34973271839"));
            if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                requestPermissions(new String[]{CALL_PHONE}, 1);
            }
        });
    }

    private void inicializarRRSS() {

        facebook.setOnClickListener(v -> {
            try {
                getContext().getPackageManager().getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/111904427124274")));
            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Pastisseria-Ronda-111904427124274")));
            }
        });
        instagram.setOnClickListener(v -> {
            try {
                getContext().getPackageManager().getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/_u/pastisseriaronda")));
            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/pastisseriaronda/")));
            }
        });
    }

}