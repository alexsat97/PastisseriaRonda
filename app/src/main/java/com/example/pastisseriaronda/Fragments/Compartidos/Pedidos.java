package com.example.pastisseriaronda.Fragments.Compartidos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.example.pastisseriaronda.Actividades.Compartidas.DetallePedido;
import com.example.pastisseriaronda.Actividades.Compartidas.DetalleProducto;
import com.example.pastisseriaronda.Auxiliares.Adapters.PedidoAdapter;
import com.example.pastisseriaronda.Auxiliares.Objetos.Pedido;
import com.example.pastisseriaronda.Auxiliares.Objetos.RegistroMantenimiento;
import com.example.pastisseriaronda.Auxiliares.Superclases.ListaRegistrosActivity;
import com.example.pastisseriaronda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

public class Pedidos extends Fragment {
    private static final int CODIGO = 5;
    private RecyclerView rv;
    private TextView empty;
    private Spinner s;
    private EditText nombre, fecha;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> list = new ArrayList<>();
    private final Map<Pedido, String> dict = new HashMap<>();
    private Date startDate, endDate;
    private AlertDialog alertDialog;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    public Pedidos() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pedidos, container, false);
        inicializar(v);
        return v;
    }

    private void inicializar(View v) {
        LinearLayout nl = v.findViewById(R.id.pnl);
        LinearLayout l = v.findViewById(R.id.pl);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PR", Context.MODE_PRIVATE);
        int type = sharedPref.getInt("empleado", 2);
        if(type != 2){
            nl.setVisibility(View.GONE);
            l.setVisibility(View.VISIBLE);
            rv = v.findViewById(R.id.lista);
            empty = v.findViewById(R.id.empty_view);
            nombre = v.findViewById(R.id.nombre);
            s = v.findViewById(R.id.spinner);
            fecha = v.findViewById(R.id.fecha);
            fecha.setFocusable(false);
            alertDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.Custom).setMessage(R.string.pedlo).setCancelable(false).build();
            seleccionFecha(v);
            seleccionNombre();
            seleccionEstado();
            obtenerPedidos();
        }else{
            nl.setVisibility(View.VISIBLE);
            l.setVisibility(View.GONE);
        }
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
                if(pedidoAdapter!=null) {
                    pedidoAdapter.setNombre(s.toString());
                    pedidoAdapter.filtrar();
                    cambiarVista(pedidoAdapter.getItemCount()==0);
                }
            }
        });
    }

    private void seleccionEstado() {
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(pedidoAdapter!=null) {
                    pedidoAdapter.setEstado(s.getSelectedItem().toString());
                    pedidoAdapter.filtrar();
                    cambiarVista(pedidoAdapter.getItemCount()==0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void seleccionFecha(View view) {
        fecha.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.calendar_popup, null);
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, focusable);
            popupWindow.showAtLocation(view.findViewById(R.id.general), Gravity.CENTER, 0, 0);
            DateRangeCalendarView calendar = popupView.findViewById(R.id.calendar);
            TextView t = popupView.findViewById(R.id.save);
            TextView c = popupView.findViewById(R.id.clear);
            calendar.setWeekOffset(1);
            final Calendar startMonth = Calendar.getInstance();
            startMonth.set(2020, Calendar.JANUARY, 0);
            //Guardar selección de las fechas
            calendar.setCalendarListener(new CalendarListener() {
                @Override
                public void onFirstDateSelected(Calendar calendar) {
                    startDate = new Date(calendar.getTimeInMillis());
                    endDate = null;
                }

                @Override
                public void onDateRangeSelected(Calendar calendar, Calendar calendar1) {
                    startDate = new Date(calendar.getTimeInMillis());
                    endDate = new Date(calendar1.getTimeInMillis());
                }
            });
            //Guardar fechas seleccionadas
            t.setOnClickListener(v1 -> {
                pedidoAdapter.clear();
                pedidoAdapter.addAll(list);
                ajustarFechas();
                popupWindow.dismiss();
            });
            //Eliminar fechas seleccionadas
            c.setOnClickListener(v12 -> {
                fecha.setText("");
                calendar.resetAllSelectedViews();
                startDate = null;
                endDate = null;
                pedidoAdapter.clear();
                pedidoAdapter.addAll(list);
                ajustarFechas();
            });
        });
    }

    protected void ajustarFechas() {
        if(startDate !=null) {
            if(endDate == null){
                fecha.setText(format.format(startDate));
                pedidoAdapter.setStart(startDate.getTime());
                pedidoAdapter.setEnd(-1);
            }else{
                fecha.setText(format.format(startDate) + " - " + format.format(endDate));
                pedidoAdapter.setStart(startDate.getTime());
                pedidoAdapter.setEnd(endDate.getTime());
            }
        }else{
            pedidoAdapter.setStart(-1);
            pedidoAdapter.setEnd(-1);
        }
        pedidoAdapter.filtrar();
        cambiarVista(pedidoAdapter.getItemCount()==0);
    }

    public void obtenerPedidos(){
        alertDialog.show();
        s.setSelection(0);
        fecha.setText("");
        list.clear();
        dict.clear();
        FirebaseFirestore.getInstance().collection("Pedidos")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> l = Objects.requireNonNull(task.getResult()).getDocuments();
                        for (DocumentSnapshot document : l) {
                            Pedido p = document.toObject(Pedido.class);
                                if(getActivity().getSharedPreferences("PR", Context.MODE_PRIVATE).getInt("empleado", 0) == 1 || p.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    list.add(p);
                                    dict.put(p, document.getId());
                                }
                        }
                        fijarAdapter();
                    }
                });
    }

    private void fijarAdapter() {
        cambiarVista(list.isEmpty());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        pedidoAdapter = new PedidoAdapter(list, getContext());
        pedidoAdapter.setOnFilteredItemClickListener(p -> {
            Intent i = new Intent(getContext(), DetallePedido.class);
            i.putExtra("nombre", p.getNombre());
            i.putExtra("foto", p.getFoto());
            i.putExtra("estado", p.getEstado());
            i.putExtra("dia", format.format(p.getFecha()));
            i.putExtra("personal", p.getNombrecliente() + " - " + p.getTelefono());
            i.putExtra("por", p.getPorciones());
            i.putExtra("pet", p.getPeticiones());
            i.putExtra("precio", p.getPrecio());
            i.putExtra("key", dict.get(p));
            startActivityForResult(i, CODIGO);
        });
        rv.setAdapter(pedidoAdapter);
        alertDialog.dismiss();
    }

    private void cambiarVista(boolean isEmpty){
        if (isEmpty){
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO) {
            if (resultCode == RESULT_OK) {
                obtenerPedidos();
            }
        }
    }
}