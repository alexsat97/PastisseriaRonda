package com.example.pastisseriaronda.Auxiliares.Superclases;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.example.pastisseriaronda.Auxiliares.Adapters.RegistroMantenimientoAdapter;
import com.example.pastisseriaronda.Auxiliares.Objetos.Producto;
import com.example.pastisseriaronda.Auxiliares.Objetos.RegistroMantenimiento;
import com.example.pastisseriaronda.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ListaRegistrosActivity extends AppCompatActivity{
    protected EditText fecha;
    protected RecyclerView rv;
    protected TextView empty;
    protected Spinner s;
    protected RegistroMantenimientoAdapter registroMantenimientoAdapter;
    protected List<RegistroMantenimiento> list = new ArrayList<>();
    private final Map<RegistroMantenimiento, String> dict = new HashMap<>();
    protected Date startDate, endDate;
    protected SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    protected FloatingActionButton floatingActionButton;
    protected AlertDialog alertDialog;

    public void inicializar(){
        getSupportActionBar().hide();
        fecha = findViewById(R.id.fecha);
        empty = findViewById(R.id.empty_view);
        registroMantenimientoAdapter = new RegistroMantenimientoAdapter(list);
        alertDialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage(R.string.obre).setCancelable(false).build();
        floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setColorFilter(Color.WHITE);
        fecha.setFocusable(false);
        s = findViewById(R.id.spinner);
        rv = findViewById(R.id.lista);

        //Gestión de la selección de una fecha
        seleccionFecha();
        //Gestión de la selección en el spinner
        seleccionSpinner();
    }

    private void seleccionFecha() {
        fecha.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.calendar_popup, null);
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, focusable);
            popupWindow.showAtLocation(findViewById(R.id.general), Gravity.CENTER, 0, 0);
            DateRangeCalendarView calendar = popupView.findViewById(R.id.calendar);
            TextView t = popupView.findViewById(R.id.save);
            TextView c = popupView.findViewById(R.id.clear);
            calendar.setWeekOffset(1);
            final Calendar startMonth = Calendar.getInstance();
            startMonth.set(2020, Calendar.JANUARY, 0);
            calendar.setSelectableDateRange(startMonth, Calendar.getInstance());

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
                registroMantenimientoAdapter.clear();
                registroMantenimientoAdapter.addAll(list);
                ajustarFechas();
                popupWindow.dismiss();
            });
            //Eliminar fechas seleccionadas
            c.setOnClickListener(v12 -> {
                fecha.setText("");
                calendar.resetAllSelectedViews();
                startDate = null;
                endDate = null;
                registroMantenimientoAdapter.clear();
                registroMantenimientoAdapter.addAll(list);
                ajustarFechas();
                popupWindow.dismiss();
            });
        });
    }

    private void seleccionSpinner() {
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                registroMantenimientoAdapter.setCampo1(s.getSelectedItem().toString());
                registroMantenimientoAdapter.filtrar();
                cambiarVista(registroMantenimientoAdapter.getItemCount()==0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void ajustarFechas() {
        if(startDate !=null) {
            if(endDate == null){
                fecha.setText(format.format(startDate));
                registroMantenimientoAdapter.setStart(startDate.getTime());
                registroMantenimientoAdapter.setEnd(-1);
            }else{
                fecha.setText(format.format(startDate) + " - " + format.format(endDate));
                registroMantenimientoAdapter.setStart(startDate.getTime());
                registroMantenimientoAdapter.setEnd(endDate.getTime());
            }
        }else{
            registroMantenimientoAdapter.setStart(-1);
            registroMantenimientoAdapter.setEnd(-1);
        }
        registroMantenimientoAdapter.filtrar();
        cambiarVista(registroMantenimientoAdapter.getItemCount()==0);
    }

    public void obtenerColección(String col){
        alertDialog.show();
        s.setSelection(0);
        fecha.setText("");
        list.clear();
        dict.clear();
        FirebaseFirestore.getInstance().collection(col)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> l = Objects.requireNonNull(task.getResult()).getDocuments();
                        for (DocumentSnapshot document : l) {
                            RegistroMantenimiento r = document.toObject(RegistroMantenimiento.class);
                            list.add(r);
                            dict.put(r, document.getId());
                        }
                        cambiarVista(list.isEmpty());
                        rv.setHasFixedSize(true);
                        rv.setLayoutManager(new LinearLayoutManager(this));
                        registroMantenimientoAdapter = new RegistroMantenimientoAdapter(list);
                        registroMantenimientoAdapter.setOnFilteredItemClickListener((r, v) -> {
                            PopupMenu p = new PopupMenu(ListaRegistrosActivity.this, v);
                            p.getMenuInflater().inflate(R.menu.popup_menu, p.getMenu());
                            p.setOnMenuItemClickListener(item -> {
                                new AlertDialog.Builder(ListaRegistrosActivity.this)
                                        .setMessage(R.string.elimreg)
                                        .setPositiveButton(R.string.si, (dialog, id) -> {
                                            FirebaseFirestore.getInstance().collection(col).document(dict.get(r)).delete();
                                            obtenerColección(col);
                                        })
                                        .setNegativeButton(R.string.can, (dialog, id) -> {}).create().show();
                                return false;
                            });
                            p.show();
                        });
                        rv.setAdapter(registroMantenimientoAdapter);
                        alertDialog.dismiss();
                    }
                });
    }

    protected void cambiarVista(boolean isEmpty){
        if (isEmpty){
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }
}
