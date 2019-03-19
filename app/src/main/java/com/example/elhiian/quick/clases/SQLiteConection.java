package com.example.elhiian.quick.clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConection extends SQLiteOpenHelper {
    public SQLiteConection(Context context) {
        super(context, "quick", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuario(id int primary key,tipodocumento text,numerodocumento text,nombres text,celular text,correo text,tipo text,estado text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
