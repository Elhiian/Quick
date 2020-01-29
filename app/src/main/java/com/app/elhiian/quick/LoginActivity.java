package com.app.elhiian.quick;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SQLiteConection;
import com.app.elhiian.quick.clases.UsuarioTemporales;
import com.app.elhiian.quick.servicios.ConsultarSolicitudes;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    ProgressBar loginProgresBar;
    Button btnLogin,btnRegister,btnRecoverPassword;
    EditText txtEmail,txtPassword;
    View view;
    ImageView imageLogo;
    CardView cardView;

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
        btnRecoverPassword=findViewById(R.id.login_recoverPassword);
        imageLogo=findViewById(R.id.imageLogo);
        cardView=findViewById(R.id.cardView);

        imageLogo.setAnimation(AnimationUtils.loadAnimation(this,R.anim.left_to_center));
        cardView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_to_up));
        stopService(new Intent(this,ConsultarSolicitudes.class));




        UsuarioTemporales.tipoUser="";
        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase db=conection.getReadableDatabase();
        db.execSQL("delete from usuario");
        db.execSQL("delete from usuarioservicio");


        stopService(new Intent(LoginActivity.this, ConsultarSolicitudes.class));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);
                btnRecoverPassword.setVisibility(View.INVISIBLE);
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

        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().length() > 0) {
                    AsyncHttpClient client=new AsyncHttpClient();
                    String url= Configuracion.servidor+"recoverpassword?email="+txtEmail.getText().toString();
                    System.out.println(url);
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode==200){
                                Toast.makeText(LoginActivity.this, "Accede a tu correo electrónico para recuperar tu clave", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(LoginActivity.this, "No pudimos restablecer tu clave", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Ingrese la direccion de correo electrónico", Toast.LENGTH_SHORT).show();
                }

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

                                }
                            }else{
                                Snackbar.make(view,"Su cuenta se encuentra deshabilitada",Snackbar.LENGTH_LONG).show();
                            }
                        }else{
                            Snackbar.make(view,"Usuario y/o contraseña incorrecta",Snackbar.LENGTH_LONG).show();
                            btnLogin.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.VISIBLE);
                            btnRecoverPassword.setVisibility(View.VISIBLE);
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

    private void saveSession(final JSONArray json) {
        SQLiteConection conection=new SQLiteConection(this);
        final SQLiteDatabase db=conection.getWritableDatabase();
        db.execSQL("delete from usuario");
        try {
            db.execSQL("insert into usuario(id,tipodocumento,numerodocumento,nombres,celular,correo,tipo,estado,clave) values" +
                    "("+json.getJSONObject(0).getString("id")+"," +
                    "'"+json.getJSONObject(0).getString("tipoDocumento")+"'," +
                    "'"+json.getJSONObject(0).getString("numeroDocumento")+"'," +
                    "'"+json.getJSONObject(0).getString("nombres")+"'," +
                    "'"+json.getJSONObject(0).getString("celular")+"'," +
                    "'"+json.getJSONObject(0).getString("correo")+"'," +
                    "'"+json.getJSONObject(0).getString("tipo")+"'," +
                    "'"+json.getJSONObject(0).getString("estado")+"'," +
                    "'"+txtPassword.getText().toString()+"')"  );

            UsuarioTemporales.tipoUser=json.getJSONObject(0).getString("tipo");

        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            if (json.getJSONObject(0).getString("tipo").equalsIgnoreCase("C")){
                AsyncHttpClient client=new AsyncHttpClient();
                client.get(Configuracion.servidor + "informacionConductor.php?id=" + json.getJSONObject(0).getString("id"), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode==200){
                            try {
                                JSONArray jsonconductor=new JSONArray(new String(responseBody));
                                db.execSQL("insert into usuarioservicio(tiposervicio) values('"+jsonconductor.getJSONObject(0).getString("tipoServicio")+"')");
                                UsuarioTemporales.tipoServicio=jsonconductor.getJSONObject(0).getString("tipoServicio");
                                Intent intent=new Intent(LoginActivity.this,MainConductorActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(LoginActivity.this, "error en el servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
