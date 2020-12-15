package com.example.pastisseriaronda.Auxiliares.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pastisseriaronda.Auxiliares.Objetos.RegistroMantenimiento;
import com.example.pastisseriaronda.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistroMantenimientoAdapter extends RecyclerView.Adapter<RegistroMantenimientoAdapter.ViewHolder> {
    private List<RegistroMantenimiento> elementos, original;
    private String campo1="", campo2="";
    private long start=-1, end=-1;
    private final List<RegistroMantenimiento> copia = new ArrayList<>();
    private RegistroListener registroListener;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    public RegistroMantenimientoAdapter(List<RegistroMantenimiento> datos) {
        elementos = new ArrayList<>(datos);
        original = new ArrayList<>(datos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_mantenimiento, null);
        itemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroMantenimiento temp =  elementos.get(position);
        holder.campo1.setText(temp.getCampo1());
        holder.campo2.setText(temp.getCampo2());
        if(temp.getCaducidad() != -1)
            holder.campo2.append("\n Cad: " + format.format(temp.getCaducidad()));
        holder.fecha.setText(format.format(temp.getFecha()));
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView campo1, campo2, fecha;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            campo1 =  itemLayoutView.findViewById(R.id.campo1);
            campo2 = itemLayoutView.findViewById(R.id.campo2);
            fecha = itemLayoutView.findViewById(R.id.fecha);
            itemLayoutView.setOnClickListener(v -> registroListener.onRegistroSelected(elementos.get(getAdapterPosition()), v));
        }
    }
    
    public void clear() {
        this.elementos.clear();
    }

    public void addAll(List<RegistroMantenimiento> elementosList) {
        this.elementos = new ArrayList<>(elementosList);
    }
    public interface RegistroListener {
        void onRegistroSelected(RegistroMantenimiento r, View v);
    }

    public void setOnFilteredItemClickListener(RegistroListener clickListener) {
        this.registroListener = clickListener;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2.toLowerCase();
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void filtrar(){
        elementos.clear();
        elementos = new ArrayList<>(original);
        filtrarCampo1();
        filtrarCampo2();
        filtrarFecha();
        notifyDataSetChanged();
    }

    private void filtrarCampo1() {
        if(campo1.equals("Todas") || campo1.equals("Todos") || campo1.isEmpty())
            copia.addAll(elementos);
        else
            for (RegistroMantenimiento item : elementos) {
                if (item.getCampo1().equals(campo1))
                    copia.add(item);
            }
        elementos.clear();
        elementos.addAll(copia);
        copia.clear();
    }

    private void filtrarCampo2() {
        if(campo2.isEmpty())
            copia.addAll(elementos);
        else
            for (RegistroMantenimiento item : elementos) {
                if(item.getCampo2().toLowerCase().equals(campo2))
                    copia.add(item);
            }
        elementos.clear();
        elementos.addAll(copia);
        copia.clear();
    }

    private void filtrarFecha() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        if(start == -1)
            return;
        if(end == -1){
            for (RegistroMantenimiento item : elementos) {
                if(fmt.format(new Date(item.getFecha())).equals(fmt.format(new Date(start))))
                    copia.add(item);
            }
        }else{
            for (RegistroMantenimiento item : elementos) {
                if(item.getFecha()>=start && item.getFecha()<= end+86400000)
                    copia.add(item);
            }
        }
        elementos.clear();
        elementos.addAll(copia);
        copia.clear();
    }
}
