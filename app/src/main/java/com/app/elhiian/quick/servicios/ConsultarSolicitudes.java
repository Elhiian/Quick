package com.app.elhiian.quick.servicios;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.widget.Toast;

import com.app.elhiian.quick.R;
import com.app.elhiian.quick.SolicitudesActivity;
import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SQLiteConection;
import com.app.elhiian.quick.clases.UsuarioTemporales;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class ConsultarSolicitudes extends Service
{
    Handler handler=new Handler();
    public static  int notificationID=0;
    public static  String chanelID="NOTIFICATION";
    private PendingIntent pendingIntent;
    public static double latActual=0.0;
    public static double longActual=0.0;
    public  boolean notifiActiveGPS=false;
    JSONArray jsonArray=null;
    SQLiteConection conection;
    SQLiteDatabase db;
    Cursor cursor;


    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flag,int idProcess){
        conection=new SQLiteConection(this);
        db=conection.getWritableDatabase();
        cursor=db.rawQuery("select tiposervicio from usuarioservicio",null);
        cursor.moveToNext();
        UsuarioTemporales.tipoServicio=cursor.getString(0);
        Cursor cursor2=db.rawQuery("select tipo from usuario",null);
        if (cursor2.moveToNext()){
            if (cursor2.getString(0).equalsIgnoreCase("C")){
                cursor2=db.rawQuery("select isactive from configuration",null);
                if (cursor2.moveToNext()){
                    if (cursor2.getString(0).equalsIgnoreCase("true")){
                        UsuarioTemporales.isActiveService=true;
                        iniciarContador();
                    }
                }

            }
        }
        return START_STICKY;
    }

    private void iniciarContador() {
        final long tiempo=7000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UsuarioTemporales.isActiveService){
                    if (serviceWorking().equalsIgnoreCase("-1")){
                        if (checkIfLocationOpened()){
                            notifiActiveGPS=false;
                            if (UsuarioTemporales.isActiveService==true){
                                miUbicacion();
                                consultarSolicitudes();
                                System.out.println("consultando solicitudes");
                            }
                        }else{
                            stopService(new Intent(ConsultarSolicitudes.this,ConsultarSolicitudes.class));
                            if (notifiActiveGPS==false){
                                Toast.makeText(ConsultarSolicitudes.this, "El gps fue desactivado Quick no estara disponible", Toast.LENGTH_SHORT).show();
                                notifiActiveGPS=true;
                            }
                        }
                    }
                    handler.postDelayed(this,tiempo);
                }
            }
        },tiempo);




    }

    private void consultarSolicitudes() {
        String url= Configuracion.servidor+ "consultarSolicitudes.php?&latitud="+ UsuarioTemporales.latitudConductor +"&longitud="+UsuarioTemporales.longitudConductor+"&tipo="+UsuarioTemporales.tipoServicio;
        AsyncHttpClient client=new AsyncHttpClient();
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONArray json=new JSONArray(new String(responseBody));
                        if (!json.getString(0).equalsIgnoreCase("none")){
                            jsonArray=json;
                            if (!verificarSolicitudesExistentes()){
                                setPengingIntent();
                                createNotificationChanel();
                                llamarNotificacion();
                            }

                        }
                    } catch (JSONException e) {

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ConsultarSolicitudes.this, "error en el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String serviceWorking(){
        String isworking="0";
        final Cursor dataInService=db.rawQuery("select * from conductorServicio where estado='active'",null);
        if (dataInService.moveToNext()){
            isworking=dataInService.getString(0);
        }else{
            isworking="-1";
        }
        dataInService.close();
        return  isworking;
    }

    private boolean verificarSolicitudesExistentes() {
        boolean exist=false;
        boolean existNew=true;
        int numeroGuardadas=0;
        for (int i=0; i< jsonArray.length(); i++){
            try {
                Cursor cursor=db.rawQuery("select * from solicitudes_recibidas where idsolicitud="+jsonArray.getJSONObject(i).getString("id"),null);
                int countResult=0;
                while (cursor.moveToNext()){
                    exist=true;
                    countResult++;
                    numeroGuardadas++;
                }
                if (countResult==0){
                    db.execSQL("insert into solicitudes_recibidas (idsolicitud , id_usuario) values('"+jsonArray.getJSONObject(i).getString("id")+"','"+jsonArray.getJSONObject(i).getString("id_usuario")+"')");
                    existNew=false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (existNew==false) exist=existNew;
        return exist;
    }


    private void setPengingIntent() {
        Intent intent=new Intent(ConsultarSolicitudes.this, SolicitudesActivity.class);
        TaskStackBuilder taskStackBuilder= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            taskStackBuilder = TaskStackBuilder.create(ConsultarSolicitudes.this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            taskStackBuilder.addParentStack(SolicitudesActivity.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            taskStackBuilder.addNextIntent(intent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pendingIntent=taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name="Notificacion";
            NotificationChannel notificationChannel=new NotificationChannel(chanelID,name,NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void llamarNotificacion() {

        String message= null;
        int ultimo=jsonArray.length()-1;
        try {
            message = jsonArray.getJSONObject(0).getString("nombres")+" \n Dirección: "+jsonArray.getJSONObject(ultimo).getString("direccion_servicio");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,chanelID);
        //validar que icono se va a presentar
        if (cursor.getString(0).equalsIgnoreCase("taxi")){
            builder.setSmallIcon(R.drawable.taxi);
        }else if (cursor.getString(0).equalsIgnoreCase("moto")){
            builder.setSmallIcon(R.drawable.motorcycle);
        }else if (cursor.getString(0).equalsIgnoreCase("acarreo")){
            builder.setSmallIcon(R.drawable.trucking);
        }else{
            builder.setSmallIcon(R.drawable.hometruck);
        }


        builder.setContentTitle("Quick");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentText(message);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.BLUE,1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);


        NotificationManagerCompat notification=NotificationManagerCompat.from(ConsultarSolicitudes.this);
        notification.notify(notificationID,builder.build());

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        UsuarioTemporales.isActiveService=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            latActual = location.getLatitude();
            longActual = location.getLongitude();
            System.out.println("latitud: "+latActual);
            System.out.println("longitud: "+longActual);

            UsuarioTemporales.latitudConductor=latActual;
            UsuarioTemporales.longitudConductor=longActual;
        }
    }



    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location!=null){
            actualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locationListener);
        }else{
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,15000,0,locationListener);
            actualizarUbicacion(location);
        }
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
