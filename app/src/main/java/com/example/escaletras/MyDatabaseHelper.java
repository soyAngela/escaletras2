package com.example.escaletras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Niveles.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "niveles";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ORIGEN = "palabra_origen";
    private static final String COLUMN_DESTINO = "palabra_destino";

    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORIGEN + " TEXT, " +
                COLUMN_DESTINO + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void anadirLibro(String origen, String destino){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ORIGEN, origen);
        cv.put(COLUMN_DESTINO, destino);
        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "No se ha podido añadir el nivel.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Se ha añadido el nivel correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor leerNiveles(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void borrarNivel(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "No se ha podido borrar.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Se ha borrado el nivel.", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarLibro(String nivel, String origen, String destino) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORIGEN, origen);
        cv.put(COLUMN_DESTINO, destino);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{nivel});
        if(result == -1){
            Toast.makeText(context, "No se ha podido actualizar el nivel.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Se ha actualizado el nivel!", Toast.LENGTH_SHORT).show();
        }
    }

    public void borraTodo(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = '" + TABLE_NAME + "';");
        anadirLibro("HOLA","CASA");
        anadirLibro("RISA", "MORA");
        anadirLibro("SOLA", "REZA");
    }
}
