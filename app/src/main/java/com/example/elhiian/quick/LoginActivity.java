package com.example.elhiian.quick;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.elhiian.quick.clases.Configuracion;
import com.example.elhiian.quick.clases.SQLiteConection;
import com.example.elhiian.quick.clases.UsuarioTemporales;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    ProgressBar loginProgresBar;
    Button btnLogin,btnRegister;
    EditText txtEmail,txtPassword;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        view=getWindow().getDecorView().getRootView();


        btnLogin=findViewById(R.id.login_button);
        btnRegister=findViewById(R.id.login_register);
        loginProgresBar=findViewById(R.id.login_progressbar);
        txtEmail=findViewById(R.id.loginEmail);
        txtPassword=findViewById(R.id.loginPassword);

        UsuarioTemporales.tipoUser="";

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);
                loginProgresBar.setVisibility(View.VISIBLE);
                consultarInformacion();

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

    }

    private void consultarInformacion() {
        String url=Configuracion.servidor+"login.php?action=login&email="+txtEmail.getText()+"&clave="+txtPassword.getText();
        System.out.println(url);
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONArray json=new JSONArray(new String(responseBody));
                        if (json.length()>0){
                            if (json.getJSONObject(0).getString("estado").equalsIgnoreCase("t")){
                                if (json.getJSONObject(0).getString("tipo").equalsIgnoreCase("U")){
                                    saveSession(json);
                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                if (json.getJSONObject(0).getString("tipo").equalsIgnoreCase("C")){
                                    saveSession(json);
                                    Intent intent=new Intent(LoginActivity.this,MainConductorActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else{
                                Snackbar.make(view,"Su cuenta se encuentra deshabilitada",Snackbar.LENGTH_LONG).show();
                            }
                        }else{
                            Snackbar.make(view,"Usuario y/o contraseña incorrecta",Snackbar.LENGTH_LONG).show();
                            btnLogin.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            loginProgresBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view,"Verifique su conexión a internet",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void saveSession(JSONArray json) {
        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase db=conection.getWritableDatabase();
        db.execSQL("delete from usuario");
        try {
            db.execSQL("insert into usuario(id,tipodocumento,numerodocumento,nombres,celular,correo,tipo,estado) values" +
                    "("+json.getJSONObject(0).getString("id")+"," +
                    "'"+json.getJSONObject(0).getString("tipoDocumento")+"'," +
                    "'"+json.getJSONObject(0).getString("numeroDocumento")+"'," +
                    "'"+json.getJSONObject(0).getString("nombres")+"'," +
                    "'"+json.getJSONObject(0).getString("celular")+"'," +
                    "'"+json.getJSONObject(0).getString("correo")+"'," +
                    "'"+json.getJSONObject(0).getString("tipo")+"'," +
                    "'"+json.getJSONObject(0).getString("estado")+"')"  );

            UsuarioTemporales.tipoUser=json.getJSONObject(0).getString("tipo");

        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
