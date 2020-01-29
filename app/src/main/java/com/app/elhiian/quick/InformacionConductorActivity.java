package com.app.elhiian.quick;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.app.elhiian.quick.clases.Configuracion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class InformacionConductorActivity extends AppCompatActivity {
    AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
    TextView textTelefono,textNombre,textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_conductor);
        textNombre=findViewById(R.id.conductorNombre);
        textTelefono=findViewById(R.id.conductorNumero);
        textEmail=findViewById(R.id.conductorCorreo);
        getUserInfo();
    }

    private void getUserInfo() {
        String url = Configuracion.servidor+"usuarioInformacion.php?id="+getIntent().getExtras().getString("id");
        System.out.println(url);
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONObject jsonObject=new JSONArray(new String(responseBody)).getJSONObject(0);
                        textNombre.setText(jsonObject.getString("nombres"));
                        textTelefono.setText(jsonObject.getString("celular"));
                        textEmail.setText(jsonObject.getString("correo"));
                    } catch (JSONException e) {
                        Toast.makeText(InformacionConductorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(InformacionConductorActivity.this, "No se pudo establecer conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
