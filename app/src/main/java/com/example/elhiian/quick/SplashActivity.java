package com.example.elhiian.quick;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.elhiian.quick.clases.SQLiteConection;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        SQLiteConection conection=new SQLiteConection(this);
        SQLiteDatabase db=conection.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from usuario",null);
        if (cursor.moveToNext()){
            if (cursor.getString(6).equalsIgnoreCase("U")){
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
            if (cursor.getString(6).equalsIgnoreCase("C")){
                startActivity(new Intent(SplashActivity.this,MainConductorActivity.class));
            }
        }else{
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        }
        finish();

    }
}
