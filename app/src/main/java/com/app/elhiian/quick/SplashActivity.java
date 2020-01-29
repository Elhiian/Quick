package com.app.elhiian.quick;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SQLiteConection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {
    private final int requestCode=0;
    boolean isActive=false;
    boolean animationActive=false;
    ImageView imageLogo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageLogo=findViewById(R.id.logoSplash);

        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},requestCode);
        }else{
            imageLogo.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.shake));
            startAnimation();
        }



    }

    public void startAnimation(){
        CountDownTimer timer=new CountDownTimer(2000,100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                imageLogo.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.shake));
                if (!animationActive){
                    animationActive=true;
                    validarSesion();
                }
                startAnimation();
            }
        }.start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==requestCode){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                validarSesion();
            }
            else{
                finish();
            }

        }
    }

    public void validarSesion (){
        if (checkIfLocationOpened()){
            SQLiteConection conection=new SQLiteConection(this);
            SQLiteDatabase db=conection.getReadableDatabase();
            final Cursor cursor=db.rawQuery("select * from usuario",null);

            if (cursor.moveToNext()){
                consultarInformacion(cursor.getString(5).toString(),cursor.getString(8).toString());
                final Handler handler=new Handler();
                final int[] intentos = {0};
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (intentos[0] < 10){
                            if (isActive){
                                if (cursor.getString(6).equalsIgnoreCase("U")){
                                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                }
                                if (cursor.getString(6).equalsIgnoreCase("C")){
                                    startActivity(new Intent(SplashActivity.this,MainConductorActivity.class));
                                }
                                finish();
                                intentos[0]=20;
                            }else{
                                if (intentos[0]==9){
                                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                                    finish();
                                }
                            }
                            intentos[0]++;
                            handler.postDelayed(this,1000);
                        }
                    }
                },500);
            }else{
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }

        }else{
            AlertDialog.Builder alert=new AlertDialog.Builder(SplashActivity.this);
            alert.setMessage("Por favor active el GPS");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    validarSesion();
                }
            });
            alert.setCancelable(false);
            alert.show();
        }


    }


    private boolean consultarInformacion(String usuario,String clave) {
        String url= Configuracion.servidor+"login.php?action=login&email="+usuario+"&clave="+clave;
        System.out.println(url);
        AsyncHttpClient client=new AsyncHttpClient();
        isActive=false;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONArray json=new JSONArray(new String(responseBody));
                        if (json.length()>0){
                            if (json.getJSONObject(0).getString("estado").equalsIgnoreCase("t")){
                                isActive=true;
                            }else{
                                Toast.makeText(SplashActivity.this, "Su cuenta se encuentra deshabilitada", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(SplashActivity.this, "La información de tu cuenta ha cambiado por favor vuelve a ingresar", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(SplashActivity.this, "verifique su conexión a internet", Toast.LENGTH_SHORT).show();;
            }
        });
        return isActive;
    }


    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("Provider contains=> " + provider);
        if (provider.contains("gps") || provider.contains("network")){
            return true;
        }
        return false;
    }



}
