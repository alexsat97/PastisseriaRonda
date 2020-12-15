package com.example.pastisseriaronda.Fragments.Compartidos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pastisseriaronda.Actividades.Compartidas.DetalleProducto;
import com.example.pastisseriaronda.Auxiliares.Adapters.ProductoAdapter;
import com.example.pastisseriaronda.Auxiliares.Objetos.Producto;
import com.example.pastisseriaronda.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;


public class Productos extends Fragment {
    private List<Producto> productos = new ArrayList();
    private final Map<Producto, String> dict = new HashMap<>();
    private CheckBox vegano, lactosa, gluten, azucar, pedido;
    private Spinner s;
    private EditText nombre;
    private TextView empty;
    private ProductoAdapter productoAdapter;
    private RecyclerView rv;
    private AlertDialog dialog;

    public Productos() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_productos, container, false);
        inicializar(vista);
        return vista;
    }

    private void inicializar(View v) {
        rv = v.findViewById(R.id.lista);
        empty = v.findViewById(R.id.empty_view);
        vegano = v.findViewById(R.id.vegano);
        lactosa = v.findViewById(R.id.lactosa);
        gluten = v.findViewById(R.id.gluten);
        azucar = v.findViewById(R.id.azucar);
        nombre = v.findViewById(R.id.nombre);
        pedido = v.findViewById(R.id.bajo);
        s = v.findViewById(R.id.spinner);

        seleccionNombre();
        seleccionTipo();
        seleccionExtras();

        if(productos.isEmpty())
            obtenerProductos();
        else
            fijarAdapter();
    }

    private void seleccionNombre() {
        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(productoAdapter!=null) {
                    productoAdapter.setNombre(s.toString());
                    productoAdapter.filtrar();
                    cambiarVista(productoAdapter.getItemCount()==0);
                }
            }
        });
    }

    private void seleccionTipo() {
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(productoAdapter!=null) {
                    productoAdapter.setTipo(s.getSelectedItem().toString());
                    productoAdapter.filtrar();
                    cambiarVista(productoAdapter.getItemCount()==0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void seleccionExtras() {
        azucar.setOnCheckedChangeListener((buttonView, isChecked) -> filtrarAlergenos(isChecked, gluten.isChecked(), lactosa.isChecked(), vegano.isChecked()));
        gluten.setOnCheckedChangeListener((buttonView, isChecked) -> filtrarAlergenos(azucar.isChecked(), isChecked, lactosa.isChecked(), vegano.isChecked()));
        lactosa.setOnCheckedChangeListener((buttonView, isChecked) -> filtrarAlergenos(azucar.isChecked(), gluten.isChecked(), isChecked, vegano.isChecked()));
        vegano.setOnCheckedChangeListener((buttonView, isChecked) -> filtrarAlergenos(azucar.isChecked(), gluten.isChecked(), lactosa.isChecked(), isChecked));
        pedido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            productoAdapter.setPedido(isChecked);
            productoAdapter.filtrar();
            cambiarVista(productoAdapter.getItemCount()==0);
        });
    }

    private void cambiarVista(boolean isEmpty) {
        if (isEmpty){
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    private void obtenerProductos(){
        dialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.Custom).setMessage(R.string.obpro).setCancelable(false).build();
        dialog.show();
        limpiarSelecciones();
        FirebaseFirestore.getInstance().collection("Productos")
                .whereEqualTo("disponible", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> l = Objects.requireNonNull(task.getResult()).getDocuments();
                        for (DocumentSnapshot document : l) {
                            Producto r = document.toObject(Producto.class);
                            dict.put(r, document.getId());
                            productos.add(r);
                        }
                        fijarAdapter();
                    }
                });
    }

    private void limpiarSelecciones() {
        dict.clear();
        productos.clear();
        vegano.setChecked(false);
        lactosa.setChecked(false);
        gluten.setChecked(false);
        azucar.setChecked(false);
        nombre.setText("");
    }

    private void fijarAdapter() {
        cambiarVista(productos.isEmpty());
        productoAdapter = new ProductoAdapter(productos, getContext());
        productoAdapter.setOnFilteredItemClickListener(p -> {
            Intent i = new Intent(getContext(), DetalleProducto.class);
            i.putExtra("nombre", p.getNombre());
            i.putExtra("foto", p.getFoto());
            i.putExtra("aler", p.getAlergenos());
            i.putExtra("desc", p.getDescripcion());
            i.putExtra("precio", p.getPrecio());
            i.putExtra("bpedido", p.isbajopedido());
            startActivity(i);
        });
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(productoAdapter);
        dialog.dismiss();
    }

    private void filtrarAlergenos(boolean azucar, boolean gluten, boolean lactosa, boolean vegano){
        productoAdapter.setVegano(vegano);
        productoAdapter.setSinAzucar(azucar);
        productoAdapter.setSinGluten(gluten);
        productoAdapter.setSinLactosa(lactosa);
        productoAdapter.filtrar();
        cambiarVista(productoAdapter.getItemCount()==0);
    }
}