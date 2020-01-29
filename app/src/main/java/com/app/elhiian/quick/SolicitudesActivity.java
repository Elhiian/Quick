package com.app.elhiian.quick;

import android.content.Intent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.elhiian.quick.Adaptadores.RecyclerSolicitudesConductor;
import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SolicitarServicio;
import com.app.elhiian.quick.clases.UsuarioTemporales;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SolicitudesActivity extends AppCompatActivity {
    ArrayList<SolicitarServicio> lisSolicitudes;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes);

        recyclerView=findViewById(R.id.recyclerSolicitudes);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                consultarSolicitudes();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        consultarSolicitudes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            startActivity(new Intent(SolicitudesActivity.this,MainConductorActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SolicitudesActivity.this,MainConductorActivity.class));
        finish();
    }

    private void consultarSolicitudes() {
        lisSolicitudes=new ArrayList<>();
        if (UsuarioTemporales.isActiveService==true){
            String url= Configuracion.servidor+ "consultarSolicitudes.php?&latitud="+ UsuarioTemporales.latitudConductor +"&longitud="+UsuarioTemporales.longitudConductor+"&tipo="+UsuarioTemporales.tipoServicio;
            System.out.println("consultar "+url);
            AsyncHttpClient client=new AsyncHttpClient();
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode==200){
                        try {
                            JSONArray json=new JSONArray(new String(responseBody));
                            if (!json.getString(0).equalsIgnoreCase("none")){
                                String s="solicitud";
                                if (json.length()>1){
                                    s+="s";
                                }
                                Toast.makeText(SolicitudesActivity.this, "Tiene  "+json.length()+" solicitudes", Toast.LENGTH_SHORT).show();
                                for (int i=0; i<json.length(); i++ ){
                                    SolicitarServicio solicitarServicio=new SolicitarServicio();
                                    solicitarServicio.setId(json.getJSONObject(i).getString("id"));
                                    solicitarServicio.setIdUsuario(json.getJSONObject(i).getString("id_usuario"));
                                    solicitarServicio.setNombres(json.getJSONObject(i).getString("nombres"));
                                    solicitarServicio.setDireccionServicio(json.getJSONObject(i).getString("direccion_servicio"));
                                    solicitarServicio.setDestino(json.getJSONObject(i).getString("destino"));
                                    solicitarServicio.setDistancia(json.getJSONObject(i).getString("distancia"));
                                    lisSolicitudes.add(solicitarServicio);
                                }
                                recyclerView.setAdapter(new RecyclerSolicitudesConductor(SolicitudesActivity.this,lisSolicitudes));

                            }else{
                                recyclerView.setAdapter(new RecyclerSolicitudesConductor(SolicitudesActivity.this,lisSolicitudes));
                                Toast.makeText(SolicitudesActivity.this, "No se encontraron solicitudes", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SolicitudesActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(SolicitudesActivity.this, "error en el servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Usted debe activar su servicio para poder mostrar las solicitudes de transporte de usuarios", Toast.LENGTH_SHORT).show();
        }
    }



}
