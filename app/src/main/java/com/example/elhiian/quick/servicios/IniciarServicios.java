package com.example.elhiian.quick.servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.elhiian.quick.clases.UsuarioTemporales;

public class IniciarServicios extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "iniciando los servicios", Toast.LENGTH_LONG).show();

        if (UsuarioTemporales.tipoUser.equalsIgnoreCase("C")){
            context.startService(new Intent(context,ConsultarSolicitudes.class));
        }else{
            context.startService(new Intent(context,ConsultarSolicitudes.class));
        }
    }
}
