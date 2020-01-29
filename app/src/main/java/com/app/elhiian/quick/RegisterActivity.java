package com.app.elhiian.quick;

import android.content.Intent;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.elhiian.quick.clases.Configuracion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    Spinner sing_up_spinner;
    String[] datos={"Cédula de Ciudadania","Documento de Ciudadano Extrangero","Registro Civil","Permiso Especial de Permanencia"};
    EditText txtDocumento,txtNombres,txtCelular,txtEmail,txtClave;
    Button btnRegistrar,btnTerminos;
    ProgressBar progressBar;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view=getWindow().getDecorView().getRootView();

        sing_up_spinner=findViewById(R.id.sing_up_spinner);
        sing_up_spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,datos));

        txtDocumento=findViewById(R.id.sing_up_documento);
        txtNombres=findViewById(R.id.sing_up_nombres);
        txtCelular=findViewById(R.id.sing_up_celular);
        txtEmail=findViewById(R.id.sing_up_email);
        txtClave=findViewById(R.id.sing_up_clave);
        progressBar=findViewById(R.id.sing_up_progressbar);
        btnRegistrar=findViewById(R.id.sing_up_btnRegistrar);
        btnTerminos=findViewById(R.id.btnTerminos);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegistrar.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                validarCampos();
            }
        });

        btnTerminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("http://movipp.com/terminos.php");
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

    }

    private void validarCampos() {
        String mensaje="";
        if (txtDocumento.getText().toString().length()>0){
            if (txtNombres.getText().toString().length()>0){
                if (txtCelular.getText().toString().length()>0){
                    if (txtEmail.getText().toString().length()>0){
                        if (txtClave.getText().toString().length()>0){
                            consultarExistente();
                        }else{
                            mensaje="Escriba su clave de acceso";
                        }
                    }else{
                        mensaje="Verifique su correo electronico";
                    }
                }else{
                    mensaje="Verifique su numero de telefono";
                }
            }else{
                mensaje="Ingrese su nombre";
            }
        }else{
            mensaje="verifique su identificación";
        }
        if (mensaje.length()>0){
            Snackbar.make(view,mensaje,Snackbar.LENGTH_LONG).show();
            btnRegistrar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    private String consultarExistente(){
        String mensaje="";
        AsyncHttpClient client=new AsyncHttpClient();
        String url=Configuracion.servidor+"register.php?tipe="+sing_up_spinner.getSelectedItem().toString()+
                "&document="+txtDocumento.getText()+"&name="+txtNombres.getText()+"&phone="+txtCelular.getText()+
                "&email="+txtEmail.getText()+"&clave="+txtClave.getText()+"&action=valide";
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONArray jsonArray=new JSONArray(new String(responseBody));
                        if (!jsonArray.getString(0).equalsIgnoreCase("true")){
                            Toast.makeText(RegisterActivity.this, ""+jsonArray.getString(0), Toast.LENGTH_SHORT).show();
                            btnRegistrar.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }else{
                            registrarInformacion();
                        }

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(RegisterActivity.this, "error en el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        return mensaje;
    }

    private void registrarInformacion() {
        String url=Configuracion.servidor+"register.php?tipe="+sing_up_spinner.getSelectedItem().toString()+
                "&document="+txtDocumento.getText()+"&name="+txtNombres.getText()+"&phone="+txtCelular.getText()+
                "&email="+txtEmail.getText()+"&clave="+txtClave.getText()+"&action=register";
        System.out.println(url);
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(RegisterActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        Snackbar.make(view,"Registro exitoso",Snackbar.LENGTH_LONG).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}








