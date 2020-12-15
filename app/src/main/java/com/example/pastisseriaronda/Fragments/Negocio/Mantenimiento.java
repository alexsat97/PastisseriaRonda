package com.example.pastisseriaronda.Fragments.Negocio;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pastisseriaronda.Actividades.Negocio.Informativas.ControlTemperaturas;
import com.example.pastisseriaronda.Actividades.Negocio.Informativas.EntradaProductos;
import com.example.pastisseriaronda.Actividades.Negocio.Informativas.Limpiezas;
import com.example.pastisseriaronda.R;

public class Mantenimiento extends Fragment {
    CardView pro, temp, limp;

    public Mantenimiento() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mantenimiento, container, false);
        inicializar(v);
        return v;
    }

    private void inicializar(View v) {
        pro = v.findViewById(R.id.prod);
        temp = v.findViewById(R.id.temp);
        limp = v.findViewById(R.id.limp);
        pro.setOnClickListener(v1 -> {
            startActivity(new Intent(getActivity(), EntradaProductos.class));
        });
        temp.setOnClickListener(v1 -> {
            startActivity(new Intent(getActivity(), ControlTemperaturas.class));
        });
        limp.setOnClickListener(v1 -> {
            startActivity(new Intent(getActivity(), Limpiezas.class));
        });
    }
}
