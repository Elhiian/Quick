package com.app.elhiian.quick.clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConection extends SQLiteOpenHelper {
    public SQLiteConection(Context context) {
        super(context, "quick", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuario(id int primary key,tipodocumento text,numerodocumento text,nombres text,celular text,correo text,tipo text,estado text,clave text)");
        db.execSQL("create table usuarioservicio(tiposervicio text)");
        db.execSQL("create table solicitudes_recibidas(idsolicitud text, id_usuario text)");
        db.execSQL("create table configuration(id text,isactive text)");
        db.execSQL("create table conductorServicio(idServicio text, estado text)");
        db.execSQL("create table solicitarservicio(id text, id_usuario text,tipo_servicio text, direccion_servicio text, destino text, estado text,fecha text, idConductor text, nombreConductor text, activo text)");
        db.execSQL("insert into configuration(id,isactive) values('1','true')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion> oldVersion){
            db.execSQL("drop table if exists usuario");
            db.execSQL("drop table if exists usuarioservicio");
            db.execSQL("drop table if exists solicitudes_recibidas");
            db.execSQL("drop table if exists configuration");
            db.execSQL("drop table if exists conductorServicio");
            db.execSQL("drop table if exists solicitarservicio");

            db.execSQL("create table usuario(id int primary key,tipodocumento text,numerodocumento text,nombres text,celular text,correo text,tipo text,estado text,clave text)");
            db.execSQL("create table usuarioservicio(tiposervicio text)");
            db.execSQL("create table solicitudes_recibidas(idsolicitud text, id_usuario text)");
            db.execSQL("create table configuration(id text,isactive text)");
            db.execSQL("create table conductorServicio(idServicio text, estado text)");
            db.execSQL("create table solicitarservicio(id text, id_usuario text,tipo_servicio text, direccion_servicio text, destino text, estado text,fecha text, idConductor text, nombreConductor text, activo text)");
            db.execSQL("insert into configuration(id,isactive) values('1','true')");
        }

    }
}
