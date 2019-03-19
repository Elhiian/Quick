package com.example.elhiian.quick;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.elhiian.quick.clases.Configuracion;
import com.example.elhiian.quick.clases.OnSwipeTouchListener;
import com.example.elhiian.quick.clases.SQLiteConection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class UbicationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    double latActual = 0.0;
    double longActual = 0.0;
    LinearLayout layoutNotificacion;
    ConstraintLayout constraintUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubication);
        layoutNotificacion=findViewById(R.id.layoutNotificacionUbicacion);
        layoutNotificacion.setVisibility(View.INVISIBLE);
        constraintUbicacion=findViewById(R.id.constraintUbicacion);
        cargarUbicacionesMap();

        constraintUbicacion.setOnTouchListener(new OnSwipeTouchListener(UbicationActivity.this){
            public void onSwipeTop() {
                Toast.makeText(UbicationActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(UbicationActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(UbicationActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(UbicationActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cargarUbicacionesMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        miUbicacion();
    }


    private void miUbicacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locationListener);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            latActual = location.getLatitude();
            longActual = location.getLongitude();
            agregarMarcador(latActual, longActual);

            if (getIntent().getExtras().getString("tipoubicacion").equalsIgnoreCase("actual")){
                consultarInformacionCoordenadas();
            }else{
                enviarSolicitudConDireccion();
            }
        }
    }

    private void agregarMarcador(double latActual, double longActual) {
        LatLng sydney = new LatLng(latActual, longActual);
        if (marcador!=null) marcador.remove();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }



    private void consultarInformacionCoordenadas() {
        AsyncHttpClient client=new AsyncHttpClient();
        String url=Configuracion.servidor+"consultarDireccion.php?lat="+latActual+"&long="+longActual;
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200)
                {
                    try {
                        JSONArray jsonArray=new JSONArray(new String(responseBody));
                        Toast.makeText(UbicationActivity.this, ""+jsonArray.getString(0), Toast.LENGTH_SHORT).show();

                        enviarSolicitud(jsonArray.getString(0));
                    } catch (Exception e) {
                        Toast.makeText(UbicationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(UbicationActivity.this, "No se pudo conectar al servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarSolicitud(String direccion) {
        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase db=conection.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from usuario",null);
        cursor.moveToNext();

        AsyncHttpClient client=new AsyncHttpClient();
        String url=Configuracion.servidor+"solicitarservicio.php?idusuario="+cursor.getString(0)+"&idusuarioservicio=0&tiposervicio="+getIntent().getExtras().getString("action")+
                "&latitud="+latActual+"&longitud="+longActual+"&tipoubicacion="+getIntent().getExtras().getString("tipoubicacion")+"&destino="+getIntent().getExtras().getString("destino")+"&direccion="+direccion;
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    cambiarEstadoBuscarConductor();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private void enviarSolicitudConDireccion() {
        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase db=conection.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from usuario",null);
        cursor.moveToNext();

        AsyncHttpClient client=new AsyncHttpClient();
        String url=Configuracion.servidor+"solicitarservicio.php?idusuario="+cursor.getString(0)+"&idusuarioservicio=0&tiposervicio="+getIntent().getExtras().getString("action")+
                "&latitud="+latActual+"&longitud="+longActual+"&tipoubicacion="+getIntent().getExtras().getString("tipoubicacion")+"&destino="+getIntent().getExtras().getString("destino")+
                "&direccion="+getIntent().getExtras().getString("direccionservicio");
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    cambiarEstadoBuscarConductor();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cambiarEstadoBuscarConductor() {
        layoutNotificacion.setVisibility(View.VISIBLE);
        Animation slide=AnimationUtils.loadAnimation(this,R.anim.slide_to_up);
        layoutNotificacion.setAnimation(slide);
    }




    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            
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



}
