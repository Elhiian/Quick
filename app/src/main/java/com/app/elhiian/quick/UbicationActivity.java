package com.app.elhiian.quick;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.elhiian.quick.clases.Configuracion;
import com.app.elhiian.quick.clases.SQLiteConection;
import com.app.elhiian.quick.servicios.ConsultarSolicitudes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class UbicationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    LocationManager locationManager;
    Marker markerConductor;
    double latActual = 0.0;
    double longActual = 0.0;
    LinearLayout layoutNotificacion,cardConsultando,cardInformacionConductor;
    ConstraintLayout constraintUbicacion;
    ImageView imgMarkerLocation;
    Button btnEnviarSolicitud,btnCancelarServicio;
    TextView labelNameConductor;
    EditText textDirection;
    ProgressBar progressBar;
    boolean isMyUbication=false;
    String direccion="";
    String direccionBuscador="";
    boolean buttonView=false;
    boolean isServiceActive=false;
    String idConductor="";
    String idServicio="";
    boolean servieAccepted=false;
    boolean restartService=false;
    CountDownTimer timerConsultarUbicacion;
    boolean serviceFinish=false;
    AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
    BitmapDescriptor icon;
    boolean cancelService=false;
    boolean locationIsCharge=false;
    boolean showMessageGPS=false;
    boolean showGetLocation=false;
    ProgressDialog alertChargeUbication;
    boolean showProgressLocation=false;

    boolean moveIdListener=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubication);
        layoutNotificacion=findViewById(R.id.layoutNotificacionUbicacion);
        layoutNotificacion.setVisibility(View.INVISIBLE);
        constraintUbicacion=findViewById(R.id.constraintUbicacion);
        imgMarkerLocation=findViewById(R.id.imgLocationMarker);
        btnEnviarSolicitud=findViewById(R.id.btnEnviarSolicitud);
        progressBar=findViewById(R.id.progressSearch);
        cardConsultando=findViewById(R.id.cardLayoutBuscando);
        cardInformacionConductor=findViewById(R.id.cardLayoutInformacionConductor);
        labelNameConductor=findViewById(R.id.labelNameConductor);
        btnCancelarServicio=findViewById(R.id.btnCancelarServicio);

        btnEnviarSolicitud.setEnabled(true);
        btnEnviarSolicitud.setVisibility(View.VISIBLE);
        btnEnviarSolicitud.startAnimation(AnimationUtils.loadAnimation(UbicationActivity.this,R.anim.slide_to_up));
        textDirection=findViewById(R.id.textDirection);

        String apykey="AIzaSyDjCEaUVWad6EZB5OXc-dzOZpaHKECd7yk";
        if (!Places.isInitialized()){
            Places.initialize(this,apykey);
        }
        PlacesClient placesClient = Places.createClient(this);
        final AutocompleteSupportFragment autocompleteSupportFragment=(AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng= place.getLatLng();
                Log.i("place apy ","on place selected: "+latLng.latitude+"\n"+latLng.longitude);
                latActual=latLng.latitude;
                longActual=latLng.longitude;
                direccionBuscador=place.getName();
                moveIdListener=false;
                agregarMarcador(latLng.latitude,latLng.longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        if (getIntent().getExtras().getString("action").equalsIgnoreCase("taxi")){
            icon=BitmapDescriptorFactory.fromResource(R.drawable.taxi);
        }else if (getIntent().getExtras().getString("action").equalsIgnoreCase("moto")){
            icon=BitmapDescriptorFactory.fromResource(R.drawable.motorcycle);
        }else if (getIntent().getExtras().getString("action").equalsIgnoreCase("acarreo")){
            icon=BitmapDescriptorFactory.fromResource(R.drawable.trucking);
        }else{
            icon=BitmapDescriptorFactory.fromResource(R.drawable.hometruck);
        }

        btnEnviarSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonView=false;
                buttonChangeView();
                enviarSolicitud();
            }
        });
        stopService(new Intent(UbicationActivity.this, ConsultarSolicitudes.class));
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Arrastre el mapa hasta la hubicación deseada", Toast.LENGTH_SHORT).show();
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
        miUbicacion();

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                buttonView=false;
                if (isServiceActive==false){
                    progressBar.setVisibility(View.VISIBLE);
                    imgMarkerLocation.setVisibility(View.VISIBLE);
                    buttonChangeView();
                }
            }
        });

    }


    private void miUbicacion() {
        if (showGetLocation==false){
            Toast.makeText(UbicationActivity.this, "Buscando su ubicación por favor espere, asegúrese de tener el GPS activado", Toast.LENGTH_LONG).show();
            showGetLocation=true;
        }
        if (!checkIfLocationOpened()){
            if (showMessageGPS==false){
                showMessageGPS=true;
                final AlertDialog.Builder alert=new AlertDialog.Builder(UbicationActivity.this);
                alert.setMessage("Por favor active su gps");
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showMessageGPS=false;
                        miUbicacion();

                    }
                });
                alert.setCancelable(false);
                alert.show();
            }
        }else{
            isMyUbication=true;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (showProgressLocation==false){
                if (alertChargeUbication!=null) alertChargeUbication.dismiss();
                alertChargeUbication=new ProgressDialog(UbicationActivity.this);
                alertChargeUbication.setMessage("Localizando su ubicación por favor espere");
                alertChargeUbication.setCancelable(false);
                alertChargeUbication.show();
                showProgressLocation=true;

            }
            if (location==null){
                location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                actualizarUbicacion(location);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2000,0,locationListener);
            }else{
                actualizarUbicacion(location);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,0,locationListener);
                if (alertChargeUbication != null) {
                    alertChargeUbication.dismiss();
                }
            }
        }


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
            locationIsCharge=true;
            if (alertChargeUbication != null) {
                alertChargeUbication.dismiss();
            }
        }else{
            locationIsCharge=false;
        }
    }

    private void agregarMarcador(double latActual, double longActual) {
        progressBar.setVisibility(View.VISIBLE);
        this.latActual=latActual;
        this.longActual=longActual;
        LatLng sydney = new LatLng(latActual, longActual);
        if (marcador!=null) marcador.remove();
        marcador=mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación"));
        if (isMyUbication==true){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        }
        obtenerDireccion();
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                imgMarkerLocation.setVisibility(View.INVISIBLE);
                if (isServiceActive==false){
                    LatLng center= mMap.getCameraPosition().target;
                    isMyUbication=false;
                    moveIdListener=true;
                    agregarMarcador(center.latitude,center.longitude);
                }
            }
        });

    }

    private  void buttonChangeView(){
        if (buttonView==false){
            btnEnviarSolicitud.setEnabled(false);
            Animation animation=AnimationUtils.loadAnimation(UbicationActivity.this,R.anim.fade_bottom);
            btnEnviarSolicitud.startAnimation(animation);
            btnEnviarSolicitud.setVisibility(View.GONE);
        }else{
            btnEnviarSolicitud.setEnabled(true);
            btnEnviarSolicitud.setVisibility(View.VISIBLE);
            btnEnviarSolicitud.startAnimation(AnimationUtils.loadAnimation(UbicationActivity.this,R.anim.slide_to_up));
        }
    }

    private void obtenerDireccion(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());
        try {
            addresses=geocoder.getFromLocation(latActual,longActual,1);
            direccion=String.format("%s ",
                    addresses.get(0).getAddressLine(0));
            direccion=direccion.replace("#","No.");
            String temp="";
            if (direccionBuscador.length()>0){
                temp=direccionBuscador+" - ";
            }
            textDirection.setText(temp+direccion);
            progressBar.setVisibility(View.GONE);
            buttonView=true;
            buttonChangeView();
            if (moveIdListener){
                direccionBuscador="";
            }
        } catch (IOException e) {
            Toast.makeText(this, "No podemos obtener sus coordenadas intente nuevamente", Toast.LENGTH_SHORT).show();
        }

    }


    /*
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
                        textDirection.setText(jsonArray.getString(0).toString());
                        direccion=jsonArray.getString(0);
                        progressBar.setVisibility(View.GONE);
                        buttonView=true;
                        buttonChangeView();
                    } catch (Exception e) {
                        Toast.makeText(UbicationActivity.this, "No se pudo conectar al servidor", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(UbicationActivity.this, "No se pudo conectar al servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    private void enviarSolicitud() {
        if (latActual!=0.0 && longActual!=0.0 && !direccion.equalsIgnoreCase("")){
            isServiceActive=true;
            imgMarkerLocation.setVisibility(View.GONE);
            SQLiteConection conection=new SQLiteConection(this);
            SQLiteDatabase db=conection.getReadableDatabase();
            Cursor cursor=db.rawQuery("select * from usuario",null);
            cursor.moveToNext();
            direccion=textDirection.getText().toString();
            AsyncHttpClient client=new AsyncHttpClient();
            String url=Configuracion.servidor+"solicitarServicio.php?idusuario="+cursor.getString(0)+"&idusuarioservicio=0&tiposervicio="+getIntent().getExtras().getString("action")+
                    "&latitud="+latActual+"&longitud="+longActual+"&tipoubicacion="+getIntent().getExtras().getString("tipoubicacion")+"&destino="+getIntent().getExtras().getString("destino")+"&direccion="+direccion;
            System.out.println(url);
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode==200){
                        try {
                            JSONArray jsonArray=new JSONArray(new String(responseBody));
                            idServicio=jsonArray.getJSONObject(0).getString("idservicio");
                        } catch (JSONException e) {
                            Toast.makeText(UbicationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        cambiarEstadoBuscarConductor();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }else{
            Toast.makeText(this, "Ocurrió un error al obtener la ubicación intente mover la cámara nuevamente.", Toast.LENGTH_SHORT).show();
        }

    }


    private void cambiarEstadoBuscarConductor() {
        layoutNotificacion.setVisibility(View.VISIBLE);
        Animation slide=AnimationUtils.loadAnimation(this,R.anim.slide_to_up);
        layoutNotificacion.setAnimation(slide);
        cardConsultando.setVisibility(View.VISIBLE);
        long cantidadSegundos=60000;
        if (getIntent().getExtras().getString("action").equalsIgnoreCase("acarreo") || getIntent().getExtras().getString("action").equalsIgnoreCase("mudanza")) {
            cantidadSegundos = 120000;
        }
        int numeroConsultas=0;
        CountDownTimer timer=new CountDownTimer(cantidadSegundos,3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                verifyRequestStatus();
                if (servieAccepted){
                    this.cancel();
                    Toast.makeText(UbicationActivity.this, "El conductor acepto el servicio", Toast.LENGTH_SHORT).show();
                    layoutNotificacion.setAnimation(AnimationUtils.loadAnimation(UbicationActivity.this,R.anim.fade_bottom));
                    layoutNotificacion.setVisibility(View.GONE);
                    guardarServicioLocal();
                }
            }

            @Override
            public void onFinish() {
                reiniciarServicio();
            }
        };
        timer.start();
    }

    private void reiniciarServicio() {
        Toast.makeText(this, "Lo sentimos no se encontrarón conductores disponibles", Toast.LENGTH_SHORT).show();
        if(!((Activity) UbicationActivity.this).isFinishing())
        {

            final AlertDialog.Builder ventana=new AlertDialog.Builder(UbicationActivity.this);
            ventana.setMessage("No se encontraron conductores disponibles para realizar el servicio, sin embargo, la operadora seguirá buscando. " +
                    "En breve nuestra operadora llamara al número registrado o enviara un mensaje por WhatsApp");
            ventana.setCancelable(false);
            ventana.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ventana.show();
        }
        layoutNotificacion.setVisibility(View.GONE);
        restartService=true;
        buttonView=true;
        cancelarServicio();
        isServiceActive=false;
        btnEnviarSolicitud.setText("Volver a intentar");
        buttonChangeView();

    }

    private void guardarServicioLocal() {
        AsyncHttpClient client=new AsyncHttpClient();
        String url =Configuracion.servidor+"obtenerInformacionServicio.php?id="+idServicio;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    try {
                        JSONArray jsonArray=new JSONArray(new String(responseBody));
                        if (jsonArray.length()>0){
                            SQLiteConection conection=new SQLiteConection(UbicationActivity.this);
                            SQLiteDatabase db=conection.getWritableDatabase();
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            String sql="insert into solicitarservicio(id, id_usuario,tipo_servicio, direccion_servicio, destino, estado,fecha, idConductor, nombreConductor, activo )values" +
                                    "('"+jsonObject.getString("id")+"','"+jsonObject.getString("id_usuario")+"','"+jsonObject.getString("tipo_servicio")+"'" +
                                    ",'"+jsonObject.getString("direccion_servicio")+"','"+jsonObject.getString("destino")+"','"+jsonObject.getString("estado")+"'" +
                                    ",'"+jsonObject.getString("fecha")+"','"+jsonObject.getString("idConductor")+"','"+jsonObject.getString("nombreConductor")+"'" +
                                    ",'"+jsonObject.getString("activo")+"')";

                            db.execSQL(sql);
                            cargarInformacionConductor(jsonObject.getString("id"),jsonObject.getString("idConductor"),jsonObject.getString("nombreConductor"));
                        }else{
                            Toast.makeText(UbicationActivity.this, "Error en el servidor intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(UbicationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarInformacionConductor(String id, final String idConductor, String nombreConductor) {
        this.idConductor=idConductor;
        this.idServicio=id;
        labelNameConductor.setText(nombreConductor);
        layoutNotificacion.setVisibility(View.VISIBLE);
        Animation slide=AnimationUtils.loadAnimation(this,R.anim.slide_to_up);
        layoutNotificacion.setAnimation(slide);
        cardConsultando.setVisibility(View.INVISIBLE);
        cardInformacionConductor.setVisibility(View.VISIBLE);
        cardInformacionConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UbicationActivity.this,InformacionConductorActivity.class).putExtra("id",idConductor));
            }
        });

        btnCancelarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelService=true;
                cancelarServicio();
            }
        });
        obtenerInformacionConductorServidor();
    }

    private void cancelarServicio() {
        String url=Configuracion.servidor+"cancelarServicio.php?idservicio="+idServicio+"&tipo=usuario";
        System.out.println(url);
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    Toast.makeText(UbicationActivity.this, "El servicio fue cancelado", Toast.LENGTH_SHORT).show();
                    if (restartService==false ){
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void obtenerInformacionConductorServidor() {
        timerConsultarUbicacion=new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                String url =Configuracion.servidor+"getUbicacionConductor.php?idservicio="+idServicio;
                System.out.println(url);
                asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode==200){
                            try {
                                JSONArray jsonArray=new JSONArray(new String(responseBody));
                                JSONObject jsonObject=jsonArray.getJSONObject(0);
                                if (!jsonObject.getString("estado").equalsIgnoreCase("cancel")){

                                    if (jsonObject.getString("estado").equalsIgnoreCase("active")){
                                        Double latitudConductor=Double.parseDouble(jsonObject.getString("latitud"));
                                        Double longitudConductor=Double.parseDouble(jsonObject.getString("longitud"));
                                        LatLng latLng=new LatLng(latitudConductor,longitudConductor);
                                        if (markerConductor!=null) markerConductor.remove();
                                        markerConductor=mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación del conductor").icon(icon));
                                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        obtenerInformacionConductorServidor();
                                    }else{
                                        if (cancelService==false){
                                            serviceFinish=true;
                                            btnCancelarServicio.setVisibility(View.GONE);
                                            abrirVentana();
                                            timerConsultarUbicacion.cancel();
                                        }
                                    }
                                }else{
                                    AlertDialog.Builder ventana=new AlertDialog.Builder(UbicationActivity.this);
                                    ventana.setTitle("Mensaje");
                                    ventana.setMessage("Lo sentimos, el servicio fue cancelado por parte del conductor vuelva a solicitar un nuevo servicio");
                                    ventana.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    ventana.setCancelable(false);
                                    ventana.show();
                                    timerConsultarUbicacion.cancel();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(UbicationActivity.this, "error al sincornizar los datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        };
        timerConsultarUbicacion.start();
    }

    private void abrirVentana() {
        AlertDialog.Builder ventana=new AlertDialog.Builder(UbicationActivity.this);
        ventana.setMessage("Su conductor ha llegado, gracias por utilizar Movipp");
        ventana.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        ventana.setCancelable(false);
        ventana.show();

    }

    @Override
    public void onBackPressed() {
        if (isServiceActive==true && serviceFinish==false){
            AlertDialog.Builder alert=new AlertDialog.Builder(UbicationActivity.this);
            alert.setMessage("Esta seguro de cancelar el servicio");
            alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteConection conection=new SQLiteConection(UbicationActivity.this);
                    SQLiteDatabase db=conection.getWritableDatabase();
                    Cursor cursor=db.rawQuery("select * from usuario",null);
                    cursor.moveToNext();
                    String url=Configuracion.servidor+"cancelarServicio.php?&idusuario="+cursor.getString(0);
                    System.out.println(url);
                    asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode==200){
                                Toast.makeText(UbicationActivity.this, "El servicio fue cancelado", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alert.show();

        }else{
            finish();
        }

    }

    private void verifyRequestStatus() {
        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase database=conection.getWritableDatabase();
        Cursor cursor=database.rawQuery("select * from usuario",null);
        if (cursor.moveToNext()){
            String url=Configuracion.servidor+"verificarSolicitudUsuario.php?id="+cursor.getString(0);
            System.out.println(url);
            AsyncHttpClient client=new AsyncHttpClient();
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode==200){
                        try {
                            JSONArray jsonArray=new JSONArray(new String(responseBody));
                            if (jsonArray.getString(0).equalsIgnoreCase("true")){
                                idConductor=jsonArray.getString(2);
                                idServicio=jsonArray.getString(1);
                                servieAccepted=true;
                            }else{
                                servieAccepted=false;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(UbicationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }else{
            Toast.makeText(this, "Regrese al login para verificar sus datos nuevamente", Toast.LENGTH_SHORT).show();
        }
        database.close();
        conection.close();
    }



    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("Provider contains=> " + provider);
        if (provider.contains("gps") || provider.contains("network")){
            return true;
        }
        return false;
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            System.out.println("latitud :"+location.getLatitude());

            if (!locationIsCharge){
                if (alertChargeUbication != null) {
                    alertChargeUbication.dismiss();
                }
                actualizarUbicacion(location);
            }
            locationManager.removeUpdates(locationListener);
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
