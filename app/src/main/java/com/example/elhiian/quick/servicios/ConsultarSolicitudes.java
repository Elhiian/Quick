package com.example.elhiian.quick.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

public class ConsultarSolicitudes extends Service
{

    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent,int flag,int idProcess){

        iniciarContador();

        return START_STICKY;
    }

    private void iniciarContador() {
        //cada sierto tiempo hace una consulta de las solicitudes de los usuarios
        final CountDownTimer timer=new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Toast.makeText(ConsultarSolicitudes.this, "el proceso termino", Toast.LENGTH_SHORT).show();
                iniciarContador();
            }
        };
        timer.start();
    }


    @Override
    public void onDestroy(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }



}
