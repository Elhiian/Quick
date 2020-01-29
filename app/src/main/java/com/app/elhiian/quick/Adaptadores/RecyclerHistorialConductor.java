package com.app.elhiian.quick.Adaptadores;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.elhiian.quick.R;
import com.app.elhiian.quick.clases.SolicitarServicio;

import java.util.ArrayList;

public class RecyclerHistorialConductor extends RecyclerView.Adapter<RecyclerHistorialConductor.Holder> {
    ArrayList<SolicitarServicio> listHistorial;
    Context context;

    public RecyclerHistorialConductor(ArrayList<SolicitarServicio> listHistorial, Context context) {
        this.listHistorial = listHistorial;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_history_conductor,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.labelDireccion.setText(listHistorial.get(i).getDireccionServicio());
        holder.labelNombreUser.setText(listHistorial.get(i).getNombres());
        holder.labelDestino.setText(listHistorial.get(i).getDestino());
        holder.labelFecha.setText(listHistorial.get(i).getFecha());
    }

    @Override
    public int getItemCount() {
        return listHistorial.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView labelDireccion,labelNombreUser,labelDestino,labelFecha;
        public Holder(@NonNull View itemView) {
            super(itemView);
            labelDireccion=itemView.findViewById(R.id.labelDireccion);
            labelNombreUser=itemView.findViewById(R.id.labelNombreUser);
            labelDestino=itemView.findViewById(R.id.labelDestino);
            labelFecha=itemView.findViewById(R.id.labelFecha);
        }
    }
}
