package com.example.elhiian.quick;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.elhiian.quick.clases.Configuracion;
import com.example.elhiian.quick.clases.SQLiteConection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class AccountActivity extends AppCompatActivity {

    Spinner sing_up_spinner;
    String[] datos={"Cédula de Ciudadania","Documento de Ciudadano Extrangero","Registro Civil","Permiso Especial de Permanencia"};
    EditText txtDocumento,txtNombres,txtCelular,txtEmail;
    Button btnRegistrar,btnChangePassword;
    ProgressBar progressBar;
    View view;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view=getWindow().getDecorView().getRootView();

        sing_up_spinner=findViewById(R.id.sing_up_spinner);
        sing_up_spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,datos));

        txtDocumento=findViewById(R.id.sing_up_documento);
        txtNombres=findViewById(R.id.sing_up_nombres);
        txtCelular=findViewById(R.id.sing_up_celular);
        txtEmail=findViewById(R.id.sing_up_email);
        progressBar=findViewById(R.id.sing_up_progressbar);
        btnRegistrar=findViewById(R.id.sing_up_btnRegistrar);
        btnChangePassword=findViewById(R.id.btnChangePassword);

        consultarInformacion();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegistrar.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                validarCampos();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModalPassword();
            }
        });
    }

    private void openModalPassword() {
        final AlertDialog.Builder ventana=new AlertDialog.Builder(AccountActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.modal_password_change_desing,null);
        final EditText txtPassOld=view.findViewById(R.id.txtPasswordOld);
        final EditText txtPassNew=view.findViewById(R.id.txtPasswordNew);
        final EditText txtPassRepeat=view.findViewById(R.id.txtPasswordRepeat);
        Button btnChange=view.findViewById(R.id.modalBntPassword);
        ProgressBar progressBar=view.findViewById(R.id.modalPassProgress);

        ventana.setView(view);
        final AlertDialog ventanaFinal=ventana.create();
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarCampos()==true){
                    validarClaveAnterior();
                }else{
                    Toast.makeText(AccountActivity.this, "Debe diligenciar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }

            private void validarClaveAnterior() {

                AsyncHttpClient client=new AsyncHttpClient();
                String url=Configuracion.servidor+"register.php?action=validePassword&pass="+txtPassOld.getText().toString()+"&user="+cursor.getString(5);
                System.out.println(url);
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode==200){
                            try {
                                JSONArray jsonArray=new JSONArray(new String(responseBody));
                                if (jsonArray.getString(0).toString().equalsIgnoreCase("true")){
                                    compararCaracteres();
                                }else{
                                    Toast.makeText(AccountActivity.this, "La clave anterior no es correcta", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(AccountActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    private void compararCaracteres() {
                        if (txtPassNew.getText().toString().equalsIgnoreCase(txtPassRepeat.getText().toString())){
                            if (!txtPassOld.getText().toString().equalsIgnoreCase(txtPassNew.getText().toString())){
                                modificarClave();
                            }else{
                                Toast.makeText(AccountActivity.this, "La nueva clave es igual a la anterior", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(AccountActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void modificarClave() {
                        String url=Configuracion.servidor+"register.php?action=updatePass&pass="+txtPassNew.getText().toString()+"&id="+cursor.getString(0);
                        System.out.println(url);
                        AsyncHttpClient client=new AsyncHttpClient();
                        client.get(url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode==200){
                                    ventanaFinal.dismiss();
                                    Toast.makeText(AccountActivity.this, "Clave modificada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }

            private boolean verificarCampos() {
                boolean retornar;
                if (txtPassOld.getText().length()>0){
                    if (txtPassNew.getText().length()>0){
                        if (txtPassRepeat.getText().length()>0){
                            retornar=true;
                        }else{retornar=false;}
                    }else{retornar=false;}
                }else{retornar=false;}
                return retornar;
            }
        });

        ventanaFinal.show();
    }

    private void consultarInformacion() {
        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase db=conection.getReadableDatabase();
        cursor=db.rawQuery("select * from usuario",null);
        if (cursor.moveToNext()){
            for (int i=0; i<datos.length; i++){
                if (cursor.getString(1).toString().equalsIgnoreCase(datos[i])){
                    sing_up_spinner.setSelection(i);
                }
            }
            txtDocumento.setText(cursor.getString(2));
            txtNombres.setText(cursor.getString(3));
            txtCelular.setText(cursor.getString(4));
            txtEmail.setText(cursor.getString(5));
        }
    }

    private void validarCampos() {
        String mensaje="";
        if (txtDocumento.getText().toString().length()>0){
            if (txtNombres.getText().toString().length()>0){
                if (txtCelular.getText().toString().length()>0){
                    if (txtEmail.getText().toString().length()>0){
                        consultarExistente();
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
                "&email="+txtEmail.getText()+"&action=valideupdate&id="+cursor.getString(0);
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONArray jsonArray=new JSONArray(new String(responseBody));
                        if (!jsonArray.getString(0).equalsIgnoreCase("true")){
                            Toast.makeText(AccountActivity.this, ""+jsonArray.getString(0), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AccountActivity.this, "error en el servidor", Toast.LENGTH_SHORT).show();
            }
        });

        return mensaje;
    }



    private void registrarInformacion() {
        String url=Configuracion.servidor+"register.php?tipe="+sing_up_spinner.getSelectedItem().toString()+
                "&document="+txtDocumento.getText()+"&name="+txtNombres.getText()+"&phone="+txtCelular.getText()+
                "&email="+txtEmail.getText()+"&action=update&id="+cursor.getString(0);
        System.out.println(url);
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    SQLiteConection conection=new SQLiteConection(AccountActivity.this);
                    SQLiteDatabase db=conection.getWritableDatabase();
                    db.execSQL("update usuario set tipoDocumento='"+sing_up_spinner.getSelectedItem()+"', " +
                            "numeroDocumento='"+txtDocumento.getText()+"', nombres='"+txtNombres.getText()+"', " +
                            "celular='"+txtCelular.getText().toString()+"' where id="+cursor.getString(0));
                    startActivity(new Intent(AccountActivity.this,MainActivity.class));
                    Snackbar.make(view,"Información actualizada",Snackbar.LENGTH_LONG).show();
                    finish();


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AccountActivity.this, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            startActivity(new Intent(AccountActivity.this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
