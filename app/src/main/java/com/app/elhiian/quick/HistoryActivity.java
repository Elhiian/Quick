package com.app.elhiian.quick;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.elhiian.quick.Adaptadores.RecyclerHistorialConductor;
import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SQLiteConection;
import com.app.elhiian.quick.clases.SolicitarServicio;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HistoryActivity extends AppCompatActivity {
    AsyncHttpClient client=new AsyncHttpClient();
    SQLiteConection conection;
    SQLiteDatabase db;
    Cursor cursor;
    RecyclerView recyclerView;
    ArrayList<SolicitarServicio> listaHistorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        conection=new SQLiteConection(this);
        db=conection.getWritableDatabase();
        cursor=db.rawQuery("select * from usuario",null);
        cursor.moveToNext();

        if (cursor.getString(6).equalsIgnoreCase("C")){
            cargarHistorial();
        }else{
            cargarHistorialUsuario();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    private void cargarHistorialUsuario() {
        Cursor history=db.rawQuery("select * from solicitarservicio",null);
        listaHistorial=new ArrayList<>();
        while (history.moveToNext()){
            SolicitarServicio servicio=new SolicitarServicio();
            servicio.setId(history.getString(0));
            servicio.setIdUsuario(history.getString(1));
            servicio.setTipoServicio(history.getString(2));
            servicio.setDireccionServicio(history.getString(3));
            servicio.setDestino(history.getString(4));
            servicio.setEstado(history.getString(5));
            servicio.setFecha(history.getString(6));
            servicio.setIdConductor(history.getString(7));
            servicio.setNombres(history.getString(8));
            servicio.setEstado(history.getString(9));
            listaHistorial.add(servicio);
        }
        RecyclerHistorialConductor adapter=new RecyclerHistorialConductor(listaHistorial,this);
        recyclerView.setAdapter(adapter);
    }

    private void cargarHistorial() {
        listaHistorial=new ArrayList<>();
        String url= Configuracion.servidor+"historialConductor.php?idconductor="+cursor.getString(0);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray=new JSONArray(new String(responseBody));
                    for (int i=0; i< jsonArray.length(); i++){
                        JSONObject servicio=jsonArray.getJSONObject(i);
                        SolicitarServicio solicitarServicio=new SolicitarServicio();
                        solicitarServicio.setId(servicio.getString("id"));
                        solicitarServicio.setIdUsuario(servicio.getString("id_usuario"));
                        solicitarServicio.setNombres(servicio.getString("nombre_usuario"));
                        solicitarServicio.setTipoServicio(servicio.getString("tipo_servicio"));
                        solicitarServicio.setDireccionServicio(servicio.getString("direccion_servicio"));
                        solicitarServicio.setDestino(servicio.getString("destino"));
                        solicitarServicio.setEstado(servicio.getString("estado"));
                        solicitarServicio.setIdConductor(servicio.getString("idConductor"));
                        solicitarServicio.setFecha(servicio.getString("fecha"));
                        solicitarServicio.setEstado(servicio.getString("activo"));
                        listaHistorial.add(solicitarServicio);
                    }

                    RecyclerHistorialConductor adapter=new RecyclerHistorialConductor(listaHistorial,HistoryActivity.this);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Toast.makeText(HistoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
