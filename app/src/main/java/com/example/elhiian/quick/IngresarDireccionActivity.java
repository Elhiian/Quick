package com.example.elhiian.quick;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IngresarDireccionActivity extends AppCompatActivity {
    Button btnSolicitarServicio;

    EditText txtDireccionServicio,txtDireccionDestino;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_direccion);
        txtDireccionServicio=findViewById(R.id.txtDireccionServicio);
        txtDireccionDestino=findViewById(R.id.txtDireccionDestino);

        btnSolicitarServicio=findViewById(R.id.btnSolicitarServicio);
        btnSolicitarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSolicitarServicio.setEnabled(false);
                if (txtDireccionServicio.getText().toString().length()>0){
                    String destino="none";
                    if (txtDireccionDestino.getText().toString().length()>0) destino=txtDireccionDestino.getText().toString();

                    Intent intent=new Intent(IngresarDireccionActivity.this,UbicationActivity.class);
                    intent.putExtra("action",getIntent().getExtras().getString("action"));
                    intent.putExtra("tipoubicacion","direccion");
                    intent.putExtra("destino",destino);
                    intent.putExtra("direccionservicio",txtDireccionServicio.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(IngresarDireccionActivity.this, "Ingrese la dirección de su servicio", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
