package com.app.elhiian.quick.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.elhiian.quick.MainConductorActivity;
import com.app.elhiian.quick.R;
import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SQLiteConection;
import com.app.elhiian.quick.clases.SolicitarServicio;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class RecyclerSolicitudesConductor extends RecyclerView.Adapter<RecyclerSolicitudesConductor.Holder> {
    ArrayList<SolicitarServicio> listSolicitudes;
    Context context;
    public RecyclerSolicitudesConductor(Context context,ArrayList<SolicitarServicio> listSolicitudes) {
        this.context=context;
        this.listSolicitudes=listSolicitudes;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_solicitudes_conductor,viewGroup,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.nombres.setText(listSolicitudes.get(i).getNombres());
        holder.direccionUbicacion.setText(listSolicitudes.get(i).getDireccionServicio());
        if (listSolicitudes.get(i).getDestino().equalsIgnoreCase("none")){
            holder.direccionDestino.setText("No especificado");
        }else{
            holder.direccionDestino.setText(listSolicitudes.get(i).getDestino());
        }

        double distancia=Double.parseDouble(listSolicitudes.get(i).getDistancia());
        double distanciaConvert=formatearDecimales(distancia,2);
        String resultado="";
        if (distancia<1){
            distancia=distanciaConvert/0.0010000;
            resultado=Double.toString(distancia)+" metros";
        }else{
            distancia=distanciaConvert;
            resultado=Double.toString(distancia)+ " kilómetros";
        }
        holder.distancia.setText(resultado);
        holder.setOnClickListener(i);
    }

    @Override
    public int getItemCount() {
        return listSolicitudes.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView nombres,direccionUbicacion,direccionDestino,distancia;
        ProgressBar progressBar;
        Button btnAceptarServicio;
        LinearLayout layoutRecyclerContent;

        public Holder(@NonNull View itemView) {
            super(itemView);

            nombres=itemView.findViewById(R.id.nombreUsaurio);
            direccionUbicacion=itemView.findViewById(R.id.direccionUbicacion);
            direccionDestino=itemView.findViewById(R.id.direccionDestino);
            distancia=itemView.findViewById(R.id.distancia);
            btnAceptarServicio=itemView.findViewById(R.id.btnAceptarServicio);
            progressBar=itemView.findViewById(R.id.progressbarsolicitud);
            layoutRecyclerContent=itemView.findViewById(R.id.layoutRecyclerContent);
            layoutRecyclerContent=itemView.findViewById(R.id.layoutRecyclerContent);

        }

        public void setOnClickListener(final int i) {
            btnAceptarServicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnAceptarServicio.setEnabled(false);
                    btnAceptarServicio.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    final SQLiteConection sqLiteConection=new SQLiteConection(context);
                    final SQLiteDatabase db=sqLiteConection.getWritableDatabase();
                    Cursor cursor=db.rawQuery("select * from usuario ",null);
                    cursor.moveToNext();
                    String url= Configuracion.servidor+"acceptService.php?idService="+listSolicitudes.get(i).getId()+"&idUser="+listSolicitudes.get(i).getIdUsuario()+"&idUserService="+cursor.getString(0);
                    System.out.println(url);
                    AsyncHttpClient client=new AsyncHttpClient();
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode==200){
                                try {
                                    JSONArray jsonArray=new JSONArray(new String(responseBody));
                                    if (jsonArray.getString(0).equalsIgnoreCase("true")){
                                        progressBar.setVisibility(View.GONE);
                                        try {
                                            db.execSQL("insert into conductorServicio(idServicio, estado) values ('"+listSolicitudes.get(i).getId()+"','active')");
                                        }catch (Exception e){
                                            Toast.makeText(context, "error al insertar", Toast.LENGTH_SHORT).show();
                                        }
                                        layoutRecyclerContent.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_top));
                                        layoutRecyclerContent.setVisibility(View.GONE);
                                        listSolicitudes.remove(i);
                                        notifyItemRemoved(i);
                                        notifyItemRangeChanged(i, listSolicitudes.size());
                                        Intent intent=new Intent(context, MainConductorActivity.class);
                                        context.startActivity(intent);
                                        ((Activity) context).finish();
                                    }else{
                                        Toast.makeText(context, "Lo sentimos el servicio ya fue asignado a otro conductor", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
            });
        }
    }

    public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
        return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
    }
}
