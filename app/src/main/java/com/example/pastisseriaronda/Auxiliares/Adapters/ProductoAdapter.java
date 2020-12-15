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
import com.example.pastisseriaronda.Auxiliares.Objetos.Producto;
import com.example.pastisseriaronda.R;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private List<Producto> productos, original;
    private final List<Producto> copia = new ArrayList<>();
    private String nombre = "", tipo = "";
    private boolean vegano = false, sinLactosa = false, sinAzucar = false, sinGluten = false, pedido = false;
    private final Context context;
    private ProductoListener pListener;

    public ProductoAdapter(List<Producto> datos, Context c) {
        productos = new ArrayList<>(datos);
        original = new ArrayList<>(datos);
        context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_producto, null);
        itemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto =  productos.get(position);
        holder.nombre.setText(producto.getNombre());
        holder.tipo.setText(producto.getTipo());
        CircularProgressDrawable drawable = new CircularProgressDrawable(context);
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        drawable.start();
        Glide.with(context).load(producto.getFoto())
                .fallback(R.mipmap.ic_launcher_round)
                .placeholder(drawable)
                .skipMemoryCache(true)
                .centerCrop()
                .into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView nombre, tipo;
        final ImageView imagen;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imagen =  itemLayoutView.findViewById(R.id.imagen);
            nombre = itemLayoutView.findViewById(R.id.nombre);
            tipo = itemLayoutView.findViewById(R.id.tipo);
            itemLayoutView.setOnClickListener(v -> pListener.onProductoSelected(productos.get(getAdapterPosition())));
        }

    }

    public interface ProductoListener { void onProductoSelected(Producto p);}

    public void setOnFilteredItemClickListener(ProductoListener clickListener) { this.pListener = clickListener; }

    public void clear() {
        this.productos.clear();
    }

    public void addAll(List<Producto> productoList) {
        this.productos = new ArrayList<>(productoList);
    }

    public void filtrar(){
        productos.clear();
        productos = new ArrayList<>(original);
        filtrarNombre();
        filtrarTipo();
        filtrarPedido();
        filtrarLactosa();
        filtrarVegano();
        filtrarGluten();
        filtrarDiabetes();
        notifyDataSetChanged();
    }

    private void filtrarNombre() {
        if(nombre.isEmpty())
            return;
        for (Producto item : productos) {
            if(item.getNombre().toLowerCase().contains(nombre))
                copia.add(item);
        }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    private void filtrarTipo() {
        if(tipo.isEmpty())
            return;
        if(tipo.equals("Todos"))
            copia.addAll(productos);
        else
            for (Producto item : productos) {
                if (item.getTipo().contains(tipo))
                    copia.add(item);
            }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    private void filtrarPedido() {
        if(!pedido)
            return;
        for (Producto item : productos) {
            if(item.isbajopedido())
                copia.add(item);
        }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    private void filtrarLactosa() {
        for (Producto item : productos) {
            if(!item.getAlergenos().toLowerCase().contains("leche") || !sinLactosa)
                copia.add(item);
        }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    private void filtrarVegano() {
        for (Producto item : productos) {
            if(!item.getAlergenos().toLowerCase().contains("leche") || !item.getAlergenos().toLowerCase().contains("huevo") || !vegano)
                copia.add(item);
        }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    private void filtrarDiabetes() {
        for (Producto item : productos) {
            if(!item.getAlergenos().toLowerCase().contains("azúcar")|| !sinAzucar)
                copia.add(item);
        }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    private void filtrarGluten() {
        for (Producto item : productos) {
            if(!item.getAlergenos().toLowerCase().contains("harina") || !sinGluten)
                copia.add(item);
        }
        productos.clear();
        productos.addAll(copia);
        copia.clear();
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPedido(boolean pedido) {
        this.pedido = pedido;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.toLowerCase();
    }

    public void setVegano(boolean vegano) {
        this.vegano = vegano;
    }

    public void setSinLactosa(boolean sinLactosa) {
        this.sinLactosa = sinLactosa;
    }

    public void setSinAzucar(boolean sinAzucar) {
        this.sinAzucar = sinAzucar;
    }

    public void setSinGluten(boolean sinGluten) {
        this.sinGluten = sinGluten;
    }
}
