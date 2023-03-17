package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    String ultima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Bundle extras = getIntent().getExtras();

        cargarPalbras();

        palabraOrigen = extras.getString("palabraOrigen").toUpperCase();
        palabraDestino = extras.getString("palabraDestino").toUpperCase();

        setPalabra(palabraDestino, 0);
        setPalabra(palabraOrigen, 1);
    }

    public void setPalabra(String palabra, Integer fila){

        for(int i = 0; i<4; i++){
            String s = "f"+fila+"c"+(i+1);
            int resID = getResources().getIdentifier(s, "id", getPackageName());
            TextView casilla = (TextView)findViewById(resID);
            casilla.setText(palabra.charAt(i)+"");
            if(palabra.charAt(i) == palabraDestino.charAt(i)){
                casilla.setBackgroundColor(Color.GREEN);
            }
        }
        ultima = palabra;
    }

    public void eliminarPalabra(View v){
        if(contPalabras>=2){
            for(int i = 0; i<4; i++){
                String s = "f"+contPalabras+"c"+(i+1);
                int resID = getResources().getIdentifier(s, "id", getPackageName());
                ((TextView)findViewById(resID)).setText("");
            }
            int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE);
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
            if(palabrasPosibles.contains(palabra) && unaLetra(ultima, palabra)){
                setPalabra(palabra, contPalabras);
                int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
                ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
                contPalabras++;
            }else if(!palabrasPosibles.contains(palabra)){
                Toast.makeText(this, "Introduce una palabra valida.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Solo puedes cambiar una letra.", Toast.LENGTH_SHORT).show();
            }
            if(palabra.equals(this.palabraDestino)){ //Victoria
                //TODO: Notificacion de lo has conseguido en X intentos

                victoria = true;
            }
        }
        else if(!victoria){
            /*int resID = getResources().getIdentifier("tableRow"+(contPalabras), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE);
            contPalabras--;*/
            Toast.makeText(this, "Has alcanzado el máximo de palabras posibles.", Toast.LENGTH_SHORT).show();
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
        Log.d("angela",palabra1);
        Log.d("angela",palabra2);
        for(int i = 0; i<palabra1.length(); i++){
            if(palabra1.charAt(i) == palabra2.charAt(i)){
                c++;
            }
        }
        Log.d("angela", "c: "+c);
        return c==(palabra1.length()-1);
    }
}