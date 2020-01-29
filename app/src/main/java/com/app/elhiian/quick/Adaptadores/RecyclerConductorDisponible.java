package com.app.elhiian.quick.Adaptadores;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.elhiian.quick.R;
import com.app.elhiian.quick.UbicationActivity;
import com.app.elhiian.quick.clases.UsuarioPrestadorServicio;

import java.util.ArrayList;

public class RecyclerConductorDisponible extends RecyclerView.Adapter<RecyclerConductorDisponible.Holder> {

    ArrayList<UsuarioPrestadorServicio> listaUsuarios;
    Context context;

    public RecyclerConductorDisponible(ArrayList<UsuarioPrestadorServicio> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerConductorDisponible.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_recycler_conductor_disponible,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerConductorDisponible.Holder holder, int i) {
        holder.nombre.setText(listaUsuarios.get(i).getNombre());
        holder.setOnClickListener(i);
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageButton btnServicio,btnInformacion;
        public Holder(@NonNull View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.listName);
            btnServicio=itemView.findViewById(R.id.btnSolicitarServicio);
            btnInformacion=itemView.findViewById(R.id.btnInformacionConductor);
        }

        public void setOnClickListener(int i) {
            btnServicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,UbicationActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
