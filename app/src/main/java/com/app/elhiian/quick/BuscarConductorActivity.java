package com.app.elhiian.quick;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.app.elhiian.quick.Adaptadores.RecyclerConductorDisponible;
import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.UsuarioPrestadorServicio;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BuscarConductorActivity extends AppCompatActivity {
    ArrayList<UsuarioPrestadorServicio> listadoUsuario;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_conductor);

        buscarConductoresDisponibles();
        recyclerView=findViewById(R.id.recyclerConductorDisponible);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void buscarConductoresDisponibles() {
        listadoUsuario=new ArrayList<>();
        String url=Configuracion.servidor+"buscarConductor.php?action="+getIntent().getExtras().getString("action");
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray json=new JSONArray(new String(responseBody));
                    for (int i=0; i<json.length(); i++){
                        UsuarioPrestadorServicio usuario=new UsuarioPrestadorServicio();
                        usuario.setIdUsuario(json.getJSONObject(i).getString("idUsuario"));
                        usuario.setFechaNacimiento(json.getJSONObject(i).getString("fechaNacimiento"));
                        usuario.setDomicilio(json.getJSONObject(i).getString("domicilio"));
                        usuario.setTipoLicencia(json.getJSONObject(i).getString("tipoLicencia"));
                        usuario.setTipoServicio(json.getJSONObject(i).getString("tipoServicio"));
                        usuario.setEstadoConductorServicio(json.getJSONObject(i).getString("estadoConductorServicio"));
                        usuario.setNombre(json.getJSONObject(i).getString("nombres"));
                        usuario.setCelular(json.getJSONObject(i).getString("celular"));
                        listadoUsuario.add(usuario);
                    }
                    RecyclerConductorDisponible adapter=new RecyclerConductorDisponible(listadoUsuario,BuscarConductorActivity.this);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(BuscarConductorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(BuscarConductorActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
