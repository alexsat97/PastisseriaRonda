package com.example.pastisseriaronda.Auxiliares.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.pastisseriaronda.Auxiliares.Objetos.Pedido;
import com.example.pastisseriaronda.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {
    private List<Pedido> pedidos, original;
    private final List<Pedido> copia = new ArrayList<>();
    private String nombre = "", estado = "";
    private long start=-1, end=-1;
    private final Context context;
    private PedidoListener pListener;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    public PedidoAdapter(List<Pedido> datos, Context c) {
        pedidos = new ArrayList<>(datos);
        original = new ArrayList<>(datos);
        context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_pedido, null);
        itemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido pedido =  pedidos.get(position);
        holder.nombre.setText(pedido.getNombre());
        holder.fecha.setText(format.format(pedido.getFecha()));
        CircularProgressDrawable drawable = new CircularProgressDrawable(context);
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        drawable.start();
        Glide.with(context).load(pedido.getFoto())
                .fallback(R.mipmap.ic_launcher_round)
                .placeholder(drawable)
                .skipMemoryCache(true)
                .centerCrop()
                .into(holder.imagen);
        switch(pedido.getEstado()){
            case "En proceso":
                holder.estado.setImageDrawable(context.getDrawable(R.drawable.proceso));
                break;
            case "Completado":
                holder.estado.setImageDrawable(context.getDrawable(R.drawable.completado));
                break;
            case "Entregado":
                holder.estado.setImageDrawable(context.getDrawable(R.drawable.entregado));
                break;
            default:
                holder.estado.setImageDrawable(context.getDrawable(R.drawable.cancelado));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView nombre, fecha;
        final ImageView imagen,estado;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imagen =  itemLayoutView.findViewById(R.id.imagen);
            nombre = itemLayoutView.findViewById(R.id.nombre);
            estado = itemLayoutView.findViewById(R.id.estado);
            fecha = itemLayoutView.findViewById(R.id.fecha);
            itemLayoutView.setOnClickListener(v -> pListener.onPedidoSelected(pedidos.get(getAdapterPosition())));
        }

    }

    public interface PedidoListener {
        void onPedidoSelected(Pedido p);
    }

    public void setOnFilteredItemClickListener(PedidoListener clickListener) {
        this.pListener = clickListener;
    }


    public void clear() {
        this.pedidos.clear();
    }

    public void addAll(List<Pedido> pedidoList) {
        this.pedidos = new ArrayList<>(pedidoList);
    }

    public void filtrar(){
        pedidos.clear();
        pedidos = new ArrayList<>(original);
        filtrarNombre();
        filtrarEstado();
        filtrarFecha();
        notifyDataSetChanged();
    }

    private void filtrarNombre() {
        if(nombre.isEmpty())
            return;
        for (Pedido item : pedidos) {
            if(item.getNombre().toLowerCase().contains(nombre) || item.getNombrecliente().toLowerCase().contains(nombre))
                copia.add(item);
        }
        pedidos.clear();
        pedidos.addAll(copia);
        copia.clear();
    }

    private void filtrarEstado() {
        if(estado.isEmpty())
            return;
        if(estado.equals("Todos"))
            copia.addAll(pedidos);
        else
            for (Pedido item : pedidos) {
                if (item.getEstado().contains(estado))
                    copia.add(item);
            }
        pedidos.clear();
        pedidos.addAll(copia);
        copia.clear();
    }

    private void filtrarFecha() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        if(start == -1)
            return;
        if(end == -1){
            for (Pedido item : pedidos) {
                if(fmt.format(new Date(item.getFecha())).equals(fmt.format(new Date(start))))
                    copia.add(item);
            }
        }else{
            for (Pedido item : pedidos) {
                if(item.getFecha()>=start && item.getFecha()<= end+86400000)
                    copia.add(item);
            }
        }
        pedidos.clear();
        pedidos.addAll(copia);
        copia.clear();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
