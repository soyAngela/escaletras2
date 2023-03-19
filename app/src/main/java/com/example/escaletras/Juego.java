package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Juego extends AppCompatActivity {

    ArrayList<String> palabrasPosibles = new ArrayList<String>();
    Integer contPalabras = 2;
    String palabraOrigen;
    String palabraDestino;
    boolean victoria = false;
    ArrayList<String> jugadas = new ArrayList<>();
    NotificationChannel channel;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Bundle extras = getIntent().getExtras();

        cargarPalbras();

        palabraOrigen = extras.getString("palabraOrigen").toUpperCase();
        palabraDestino = extras.getString("palabraDestino").toUpperCase();

        jugadas.add(palabraOrigen);

        setPalabra(palabraDestino, 0);
        setPalabra(palabraOrigen, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel = new NotificationChannel("victoria", "notificacionVictoria", NotificationManager.IMPORTANCE_HIGH);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){
        jugadas = savedInstanceState.getStringArrayList("jugadas");
        for(int i = 1; i<jugadas.size(); i++){
            setPalabra(jugadas.get(i), i+1);
            int resID = getResources().getIdentifier("tableRow"+(i+2), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
        }
        contPalabras = jugadas.size()+1;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("jugadas", jugadas);

    }

    public void setPalabra(String palabra, Integer fila){

        for(int i = 0; i<4; i++){
            String s = "f"+fila+"c"+(i+1);
            int resID = getResources().getIdentifier(s, "id", getPackageName());
            TextView casilla = (TextView)findViewById(resID);
            casilla.setText(palabra.charAt(i)+"");

            if(palabra.charAt(i) == palabraDestino.charAt(i) && fila != 0){
                casilla.setBackgroundColor(Color.GREEN);
            }
        }
    }

    public void eliminarPalabra(View v){
        if(contPalabras>2){
            for(int i = 0; i<4; i++){
                String s = "f"+(contPalabras-1)+"c"+(i+1);
                int resID = getResources().getIdentifier(s, "id", getPackageName());
                TextView casilla = findViewById(resID);
                casilla.setText("");
                casilla.setBackgroundResource(R.drawable.back);
            }
            int resID = getResources().getIdentifier("tableRow"+(contPalabras), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE);
            jugadas.remove(jugadas.size()-1);
            contPalabras--;
            Log.d("contPalabras: ", contPalabras+"");
        }
        if(victoria){
            victoria = false;
        }
    }

    public void anadirPalabra(View v){
        String palabraMin = (((TextView) findViewById(R.id.nuevaPalabra)).getText()).toString();
        String palabra = palabraMin.toUpperCase();

        if(contPalabras<=6 && !victoria){

            if(palabrasPosibles.contains(palabra) && unaLetra(jugadas.get(jugadas.size()-1), palabra)){
                setPalabra(palabra, contPalabras);

                if(palabra.equals(this.palabraDestino)){ //Victoria
                    //TODO: Notificacion de lo has conseguido en X intentos
                    builder = new NotificationCompat.Builder(this, "victoria");
                    if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 11);
                    }
                    builder.setSmallIcon(android.R.drawable.btn_star_big_on)
                            .setContentTitle("Enhorabuena!")
                            .setContentText("Lo has conseguido en "+ (jugadas.size()) +" palabras.")
                            .setSubText("Victoria")
                            .setVibrate(new long[]{0, 1000, 500, 1000})
                            .setAutoCancel(true);
                    notificationManager.notify(1, builder.build());

                    victoria = true;
                }else if(contPalabras<6){
                    int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
                    ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
                    ((EditText) findViewById(R.id.nuevaPalabra)).setText("");
                }else{
                    Toast.makeText(this, "Has alcanzado el máximo de palabras posibles.", Toast.LENGTH_SHORT).show();
                }
                jugadas.add(palabra);
                contPalabras++;
            }else if(!palabrasPosibles.contains(palabra)){
                Toast.makeText(this, "Introduce una palabra valida.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Solo puedes cambiar una letra.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cargarPalbras(){
        InputStream fich = getResources().openRawResource(R.raw.palabras);
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
        String linea;
        try {
            while ((linea = buff.readLine()) != null){
                String[] palabras = linea.split(" ");
                for(String palabra : palabras) {
                    palabrasPosibles.add(palabra.toUpperCase());
                }
            }
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean unaLetra(String palabra1, String palabra2){
        int c = 0;
        for(int i = 0; i<palabra1.length(); i++){
            if(palabra1.charAt(i) == palabra2.charAt(i)){
                c++;
            }
        }
        return c==(palabra1.length()-1);
    }
}